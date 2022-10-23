package com.bluejungle.destiny.server.security.secureConnector;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Base64;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bluejungle.framework.crypt.IDecryptor;
import com.bluejungle.framework.crypt.ReversibleEncryptor;

public class SecureStore {
	
	private static final Log LOG = LogFactory.getLog(SecureStore.class);
	
	private static final IDecryptor DECRYPTOR = new ReversibleEncryptor();
	private static final String CONFIGURATION_FOLDER = System.getProperty("server.config.path");
	private static final String CERTIFICATE_FOLDER = String.join(File.separator, System.getProperty("cc.home"), "server", "certificates");
	private static final String OVERRIDE_FOLDER_NAME = "override";
	public static final String CIPHER_VALUE_PREFIX = "{cipher}";

	private static final String KEY_STORE_PASSWORD = "key.store.password";
	private static final String TRUST_STORE_PASSWORD = "trust.store.password";
	
	private static final String DB_PROPERTY_DRIVER = "db.driver";
	private static final String DB_PROPERTY_URL = "db.url";
	private static final String DB_PROPERTY_USERNAME = "db.username";
	private static final String DB_PROPERTY_PASSWORD = "db.password";
	
	private static final String STORES_PASSWORD_QUERY = "SELECT CONFIG_KEY, VALUE FROM SYS_CONFIG WHERE CONFIG_KEY LIKE '%.store.password'";
	private static final String STORE_FILES_QUERY = "SELECT NAME, STORE_FILE, HASH_ALGORITHM, CHECKSUM FROM SECURE_STORE";
	private static final String STORE_FILE_HASH_ALGORITHM_QUERY = "SELECT HASH_ALGORITHM FROM SECURE_STORE WHERE NAME = ?";
	private static final String STORE_FILE_UPDATE = "UPDATE SECURE_STORE SET STORE_FILE = ?, CHECKSUM = ?, MODIFIED_ON = ? WHERE NAME = ?";
	
	private static final String CONFIG_KEY_FIELD = "CONFIG_KEY";
	private static final String CONFIG_VALUE_FIELD = "VALUE";
	
	private static final String SECURE_STORE_NAME_FIELD = "NAME";
	private static final String SECURE_STORE_FILE_FIELD = "STORE_FILE";
	private static final String SECURE_STORE_HASH_ALGORITHM_FIELD = "HASH_ALGORITHM";
	private static final String SECURE_STORE_CHECKSUM_FIELD = "CHECKSUM";
	
	private static final String APPLICATION_PROPERTIES_FILE = "application.properties";
	private static final String BOOTSTRAP_PROPERTIES_FILE = "bootstrap.properties";
	private static final String STORES_ZIP_FILE = "stores.zip";
	
	private static final int BUFFER_SIZE = 1024;
	
    private static final String GET = "GET";
    private static final String AUTHORIZATION = "Authorization";
    private static final String BASIC_TEMPLATE = "Basic %s";
	
	private String keystorePassword = null;
	private String truststorePassword = null;
	
	/**
	 * Secure store setup constructor.
	 * This class will retrieve key store and trust store password.
	 * This class will retrieve key store and trust store files.
	 */
	public SecureStore()  {
		try {
			init();
		} catch(Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}
	
	/**
	 * Get the key store password.
	 * @return Encrypted key store password to access key store files
	 */
	public String getKeystorePass() {
		return this.keystorePassword;
	}
	
	/**
	 * Get the trust store password.
	 * @return Encrypted trust store password to access trust store files
	 */
	public String getTruststorePass() {
		return this.truststorePassword;
	}
	
	/**
	 * Detect existence of application.properties file.
	 * 	If exist, setup store and information from database.
	 *  Else detect existence of bootstrap.properties file and set up store and information through configuration service.
	 * @throws Exception All kind of SQL or IO exceptions
	 */
	private void init() 
			throws Exception {
		File applicationPropertiesFile = new File(CONFIGURATION_FOLDER, APPLICATION_PROPERTIES_FILE);
		
		if(applicationPropertiesFile.exists()) {
			Properties dbConnProperties = readProperties(applicationPropertiesFile); 
			
			overrideSecureStores(dbConnProperties);
			setupThroughDatabase(dbConnProperties);
			zipSecureStores();
		} else {
			File bootstrapPropertiesFile = new File(CONFIGURATION_FOLDER, BOOTSTRAP_PROPERTIES_FILE);
			
			if(bootstrapPropertiesFile.exists()) {
				setupThroughService(readProperties(bootstrapPropertiesFile));
			}
		}
	}
	
	/**
	 * Populate properties from properties file.
	 * @param propertiesFile File contains properties to load from
	 * @return Properties loaded from given file.
	 * @throws IOException
	 */
	private Properties readProperties(File propertiesFile)
			throws IOException {
		Properties properties = new Properties();
		
		properties.load(new FileInputStream(propertiesFile));
		
		return properties;
	}
	
	/**
	 * Override secure store files from override folder if file exists
	 * @param properties Database connection properties
	 * @throws ClassNotFoundException If given database driver class not found
	 * @throws SQLException Database exception
	 * @throws IOException File operation exception
	 * @throws NoSuchAlgorithmException Invalid file checksum digest algorithm 
	 */
	private void overrideSecureStores(Properties properties)
			throws ClassNotFoundException, SQLException, IOException, NoSuchAlgorithmException {
		File overrideFolder = new File(CERTIFICATE_FOLDER, OVERRIDE_FOLDER_NAME);
		
		if(overrideFolder.exists()) {
			File[] overrideFiles = overrideFolder.listFiles(File::isFile);
			
			if(overrideFiles.length == 0)
				return;
			
			Class.forName(properties.getProperty(DB_PROPERTY_DRIVER));
			String dbPassword = properties.getProperty(DB_PROPERTY_PASSWORD);
			try(Connection connection = DriverManager.getConnection(properties.getProperty(DB_PROPERTY_URL), 
				properties.getProperty(DB_PROPERTY_USERNAME),
					dbPassword == null || dbPassword.isEmpty() ? null : decryptIfEncrypted(dbPassword))) {
				for(File file : overrideFiles) {
					try(PreparedStatement selectPreparedStatement = connection.prepareStatement(STORE_FILE_HASH_ALGORITHM_QUERY)) {
						selectPreparedStatement.setString(1, file.getName());
						
						try(ResultSet resultSet = selectPreparedStatement.executeQuery()) {
							if(resultSet.next()) {
								String hashAlgorithm = resultSet.getString(SECURE_STORE_HASH_ALGORITHM_FIELD);
								
								try(PreparedStatement updatePreparedStatement = connection.prepareStatement(STORE_FILE_UPDATE);
										InputStream inputStream = new FileInputStream(file)) {
									updatePreparedStatement.setBinaryStream(1, inputStream, file.length());
									updatePreparedStatement.setString(2, bytesToHex(Hash.valueOf(hashAlgorithm).checksum(file)));
									updatePreparedStatement.setDate(3, new Date(System.currentTimeMillis()));
									updatePreparedStatement.setString(4, file.getName());
									
									updatePreparedStatement.execute();
								}
								
								Files.deleteIfExists(file.toPath());
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * Load store passwords and files from database
	 * @param properties Database connection properties
	 * @throws ClassNotFoundException If given database driver class not found
	 * @throws SQLException Database exception
	 * @throws IOException File operation exception
	 * @throws NoSuchAlgorithmException Invalid file checksum digest algorithm 
	 */
	private void setupThroughDatabase(Properties properties) 
			throws ClassNotFoundException, SQLException, IOException, NoSuchAlgorithmException {
		Class.forName(properties.getProperty(DB_PROPERTY_DRIVER));

		String dbPassword = properties.getProperty(DB_PROPERTY_PASSWORD);
		try(Connection connection = DriverManager.getConnection(properties.getProperty(DB_PROPERTY_URL), 
				properties.getProperty(DB_PROPERTY_USERNAME),
				dbPassword == null || dbPassword.isEmpty() ? null : decryptIfEncrypted(properties.getProperty(DB_PROPERTY_PASSWORD)));
				Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {
			// Read store passwords
			try(ResultSet resultSet = statement.executeQuery(STORES_PASSWORD_QUERY)) {
				String configKey;
				String value;
				
				while(resultSet.next()) {
					configKey = resultSet.getString(CONFIG_KEY_FIELD);
					value = resultSet.getString(CONFIG_VALUE_FIELD);
					
					if(KEY_STORE_PASSWORD.equals(configKey)) {
						this.keystorePassword = value;
						continue;
					}
					
					if(TRUST_STORE_PASSWORD.equals(configKey)) {
						this.truststorePassword = value;
					}
				}
			}
			
			// Read store files
			try(ResultSet resultSet = statement.executeQuery(STORE_FILES_QUERY)) {
				String name;
				String hashAlgorithm;
				String checksum;
				
				while(resultSet.next()) {
					name = resultSet.getString(SECURE_STORE_NAME_FIELD);
					hashAlgorithm = resultSet.getString(SECURE_STORE_HASH_ALGORITHM_FIELD);
					checksum = resultSet.getString(SECURE_STORE_CHECKSUM_FIELD);
					
					File secureStore = new File(CERTIFICATE_FOLDER, name);
					if(!secureStore.exists()
							|| !checksum.equals(bytesToHex(Hash.valueOf(hashAlgorithm).checksum(secureStore)))) {
						byte[] secureStoreContent = resultSet.getBytes(SECURE_STORE_FILE_FIELD);
						if(secureStoreContent != null) {
							try (FileOutputStream fileOutputStream = new FileOutputStream(secureStore)) {
								fileOutputStream.write(secureStoreContent, 0, secureStoreContent.length);
								fileOutputStream.flush();
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * Load store passwords and files from configuration service
	 * @param properties Configuration service connection properties
	 * @throws InterruptedException Thread sleep interrupted exception
	 */
	private void setupThroughService(Properties properties) 
			throws InterruptedException {
        byte[] authorization = String.format("%s:%s", properties.get("spring.cloud.config.username"),
        		decryptIfEncrypted(properties.getProperty("spring.cloud.config.password")))
                .getBytes(StandardCharsets.UTF_8);
        String configurationPropertiesURL = String.format("%s/%s", properties.get("spring.cloud.config.uri"), "application-default.properties");
        String secureStoreURL = String.format("%s/%s/%s", properties.get("spring.cloud.config.uri"), "secure-store", "download");
        String encodedAuthorization = Base64.getEncoder().encodeToString(authorization);
        
        setupSecureStorePassword(configurationPropertiesURL, encodedAuthorization);
        setupSecureStores(secureStoreURL, encodedAuthorization);
	}
	
	/**
	 * Call to config service and retrieve key and trust store passwords
	 * @param url Config service URL
	 * @param encodedAuthorization authentication information
	 * @throws InterruptedException when process is interrupted between retries
	 */
	private void setupSecureStorePassword(String url, String encodedAuthorization) 
			throws InterruptedException {
		HttpURLConnection connection = null;
		
        while(true) {
	        try {
	            connection = (HttpURLConnection) new URL(url).openConnection();
	            connection.setRequestMethod(GET);
	            connection.setRequestProperty(AUTHORIZATION,
	                    String.format(BASIC_TEMPLATE, encodedAuthorization));
	            
	            try(InputStream inputStream = connection.getInputStream();
	            		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                    byte[] buffer = new byte[BUFFER_SIZE];
                    int length;
                    while((length = inputStream.read(buffer)) > 0) {
                        byteArrayOutputStream.write(buffer, 0, length);
                    }
                    byteArrayOutputStream.flush();
                    
                    Properties systemConfig = new Properties();
                    systemConfig.load(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
                    
                    this.keystorePassword = systemConfig.getProperty(KEY_STORE_PASSWORD);
                    this.truststorePassword = systemConfig.getProperty(TRUST_STORE_PASSWORD);
                    
                    break;
	            }
	        } catch(IOException err) {
	        	LOG.warn("Unable to connect system configuration service, retry 5 seconds later.", err);
	            
	        	if(LOG.isDebugEnabled() && connection != null) {
	            	LOG.debug("URL: " + connection.getURL().toString(), err);
	            }

	        	try {
					Thread.sleep(5000L);
				} catch (InterruptedException e) {
					LOG.error("Error occured.", e);
					throw e;
				}
	        } finally {
	            if(connection != null) {
	                connection.disconnect();
	                connection = null;
	            }
	        }
        }
	}
	
	/**
	 * Download secure stores from config service
	 * @param url Config service url
	 * @param encodedAuthorization authentication information
	 * @throws InterruptedException when process is interrupted between retries
	 */
	private void setupSecureStores(String url, String encodedAuthorization)
			throws InterruptedException {
		HttpURLConnection connection = null;
		
		while(true) {
	        try {
	            connection = (HttpURLConnection) new URL(url).openConnection();
	            connection.setRequestMethod(GET);
	            connection.setRequestProperty(AUTHORIZATION,
	                    String.format(BASIC_TEMPLATE, encodedAuthorization));

	        	try(InputStream inputStream = connection.getInputStream();
	        			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
        			byte[] buffer = new byte[BUFFER_SIZE];
        			int length;
        			while((length = inputStream.read(buffer)) > 0) {
        				byteArrayOutputStream.write(buffer, 0, length);
        			}
        			byteArrayOutputStream.flush();
        			
        			try(ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()))) {
        				ZipEntry fileEntry = zipInputStream.getNextEntry();
        				
        				while(fileEntry != null) {
        					try(ByteArrayOutputStream entryOutputStream = new ByteArrayOutputStream()) {
        						while((length = zipInputStream.read(buffer)) > 0) {
        							entryOutputStream.write(buffer, 0, length);
        						}
        						entryOutputStream.flush();
        						
        						writeStoreFile(fileEntry.getName(), entryOutputStream.toByteArray());
        					}
        					
        					fileEntry = zipInputStream.getNextEntry();
        				}
        				
        				zipInputStream.closeEntry();
        				break;
        			}
	        	}
	        } catch(IOException err) {
	        	LOG.warn("Unable to connect secure store service, retry 5 seconds later.", err);
	        	
	            if(LOG.isDebugEnabled() && connection != null) {
	            	LOG.debug("URL: " + connection.getURL().toString(), err);
	            }

	        	try {
					Thread.sleep(5000L);
				} catch (InterruptedException e) {
					LOG.error("Error occured.", e);
					throw e;
				}
	        } catch(NoSuchAlgorithmException e) {
	        	LOG.error(e.getMessage(), e);
	        } finally {
	            if(connection != null) {
	                connection.disconnect();
	                connection = null;
	            }
	        }
        }
	}
	
	/**
	 * Process safe file overwrite through obtain exclusive file lock. Only over write the file if checksum is not match.
	 * @param fileName
	 * @param newFileContent
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 */
	private void writeStoreFile(String fileName, byte[] newFileContent)
			throws IOException, NoSuchAlgorithmException, InterruptedException {
		File secureStore = new File(CERTIFICATE_FOLDER, fileName);
		
		if(secureStore.exists()) {
			while(true) {
				if(bytesToHex(Hash.MD5.checksum(secureStore)).equals(bytesToHex(Hash.MD5.checksum(newFileContent))))
					return;
				
				try(FileOutputStream secureStoreOutputStream = new FileOutputStream(secureStore);
					FileLock secureStoreLock = secureStoreOutputStream.getChannel().tryLock()) {
					if(null != secureStoreLock) {
						secureStoreOutputStream.write(newFileContent);
						secureStoreOutputStream.flush();
						LOG.info(String.format("Secure store file %s updated.", fileName));
						return;
					}
				} catch(OverlappingFileLockException e) {
					Thread.sleep(1000L);
					LOG.error(e.getMessage());
				}
			}
		} else {
			try(FileOutputStream secureStoreOutputStream = new FileOutputStream(secureStore)) {
				secureStoreOutputStream.write(newFileContent);
				secureStoreOutputStream.flush();
			}
		}
	}
	
	/**
	 * Zip all secure store files into a single compressed file.
	 * This file will be used by config server to stream to config client afterwards.
	 */
	private void zipSecureStores() 
			throws IOException {
		File[] filesToZip = (new File(CERTIFICATE_FOLDER)).listFiles(file -> 
								file.isFile() 
									&& !file.getName().equalsIgnoreCase(STORES_ZIP_FILE));
		File zipFile = new File(CERTIFICATE_FOLDER, STORES_ZIP_FILE);
		
		try(ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFile))) {
			byte[] buffer = new byte[BUFFER_SIZE];
			int length;
			
			for(File candidate : filesToZip) {
				try(FileInputStream fileInputStream = new FileInputStream(candidate)) {
					ZipEntry zipEntry = new ZipEntry(candidate.getName());
					zipOutputStream.putNextEntry(zipEntry);
					
					while((length = fileInputStream.read(buffer)) > 0) {
						zipOutputStream.write(buffer, 0, length);
					}
					zipOutputStream.flush();
				}
			}
		}
	}
	
	/**
	 * Convert bytes into hexadecimal expression.
	 * @param hashInBytes hash value
	 * @return hash in hexadecimal expression
	 */
	private String bytesToHex(byte[] hashInBytes) {
		StringBuilder stringBuilder = new StringBuilder();
		
		for(byte b : hashInBytes) {
			stringBuilder.append(String.format("%02x", b));
		}
		
		return stringBuilder.toString().toUpperCase();
	}

	private String decryptIfEncrypted(String password) {
		return password.startsWith(CIPHER_VALUE_PREFIX) ? DECRYPTOR.decrypt(password) : password;
	}
}
