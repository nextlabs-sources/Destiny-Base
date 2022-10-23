/*
 * Created on Jun 25, 2021
 *
 * All sources, binaries and HTML pages (C) copyright 2021 by NextLabs Inc.,
 * San Mateo CA, Ownership remains with NextLabs Inc, All rights reserved
 * worldwide.
 *
 * @author amorgan
 */

package com.nextlabs.destiny.tools.dbinit.hibernate.dialect;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import net.sf.hibernate.dialect.Dialect;

import com.bluejungle.destiny.tools.dbinit.hibernate.dialect.DialectExtended;
import com.bluejungle.destiny.tools.dbinit.hibernate.mapping.ColumnM;

public class DB2DialectX extends DialectExtended {
    /**
     * Constructor
     */
    public DB2DialectX(Dialect dialect) {
        super(dialect);
    }

    private static final ArrayList<Set<Short>> db2DialectTypesMap = new ArrayList<Set<Short>>();
    
    static {
        TreeSet<Short> list = new TreeSet<Short>();

        list.add((short)Types.BIT);
        list.add((short)Types.BIGINT);
        list.add((short)Types.SMALLINT);
        list.add((short)Types.TINYINT);
        list.add((short)Types.INTEGER);
        list.add((short)Types.NUMERIC);
        list.add((short)Types.DECIMAL);
        db2DialectTypesMap.add(list);
        
        list = new TreeSet<Short>();
        list.add((short)Types.REAL );
        list.add((short)Types.DOUBLE );
        db2DialectTypesMap.add(list);

        list = new TreeSet<Short>();
        list.add((short)Types.DATE);
        list.add((short)Types.TIMESTAMP);
        list.add((short)Types.TIME);
        db2DialectTypesMap.add(list);
        
        list = new TreeSet<Short>();
        list.add((short)Types.BLOB);
		list.add((short)Types.BINARY);
        list.add((short)Types.VARBINARY);
        db2DialectTypesMap.add(list);
        
        list = new TreeSet<Short>();
        list.add((short)Types.VARCHAR);
        list.add((short)Types.NVARCHAR);
        list.add((short)Types.CLOB);
        db2DialectTypesMap.add(list);
    }

	@Override
	public String getDefaultValue(short dataType) {
		switch (dataType) {
		case Types.BIT:
		case Types.BIGINT:
		case Types.SMALLINT:
		case Types.TINYINT:
		case Types.INTEGER:
		case Types.FLOAT:
		case Types.DOUBLE:
		case Types.NUMERIC:
		case Types.DECIMAL:
			return "0";
		case Types.CHAR:
		case Types.VARCHAR:
		case Types.VARBINARY:
		case Types.BLOB:
		case Types.CLOB:
			return "' '";
		case Types.DATE:
		case Types.TIMESTAMP:
		case Types.TIME:
			return "'2000-01-01'";
		default:
			throw new IllegalArgumentException("Type is invalid " + dataType + " in " + this.toString());
		}
	}

    @Override
    public String getDropColumnString() {
        return "DROP COLUMN";
    }
    
    @Override
	public String getAlterColumnString() {
		return "ALTER COLUMN";
	}

    @Override
    public String getSetNullableString(String sameType, boolean setNull) {
        return setNull ? " NULL " : " NOT NULL ";
    }
    
    @Override
    public String sqlAlterColumnType(String tableName, String columnName, String newType) {
        return ALTER_TABLE + "'"+ tableName + "' " + getAlterColumnString() + " '" + columnName + "' SET DATA TYPE " + newType;
    }

    @Override
    public String getLengthString() {
        return "LENGTH";
    }
    
    @Override
	public boolean isSequenceExist(Connection connection, String name) {
        try (Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery("VALUES NEXT VALUE FOR " + name);
            return rs.next();
        } catch (SQLException e) {
            return false;
        }
    }
    
    @Override
    public String sqlRebuildIndex(String indexName, String tableName) {
        // Unable to find a way to rebuild indexes using DB2 SQL. REORG INDEX and related
        // statements are DB2 commands, and not strictly SQL.
        //
        // Hope that the DB is smart enough to recalculate when it needs to
        return "";
    }

    @Override
    public String getLargeStringDataType(int unused) {
        return "CLOB";
    }
    
    @Override
    public String getTableSchema(DatabaseMetaData meta) throws SQLException {
        try (Statement statement = meta.getConnection().createStatement()) {
            // DB2 supports multiple schemas per table, but we ony ever have one
            ResultSet rs = statement.executeQuery("VALUES CURRENT SCHEMA");
            if (rs.next()) {
                return rs.getString(1);
            }

            return null;
        } catch (SQLException e) {
            return null; 
        }
    }
    
	@Override
	public int compareLength(ColumnM dbColumnM, ColumnM hibColumnM) {
		if (dbColumnM.getColumnSize() != hibColumnM.getColumnSize()) {
            //length is different

            //this item could be a custom type
            String hibTypeName = hibColumnM.getSqlTypeName();

            //if custom type is not found, get type from dialect
            if(hibTypeName == null) {
            	hibTypeName = hibColumnM.getDialectType(this);
            }

            String dbTypeName = dbColumnM.getDialectType(this);
            
            if(hibTypeName.equalsIgnoreCase(dbTypeName)) {
                return 0;
            } else {
                return dbColumnM.getColumnSize() - hibColumnM.getColumnSize();
            }
        }else{
            return 0;
        }
	}
    
	@Override
	public boolean isSameType(short c1DataType, short c2DataType) {
		if (c1DataType != c2DataType) {
            for(Set<Short> typeSet:db2DialectTypesMap){
                if(typeSet.contains(c1DataType) && typeSet.contains(c2DataType)){
                    return true;
                }
            }
            return false;
        }else{
            return true;
        }
	}

    @Override
    public List<String> adjustGeneratedSQL(String sql) {
        if (sql == null) {
            return null;
        }

        if (sql.startsWith("create table DICT_ELEMENTS ")) {
            return Arrays.asList("create table DICT_ELEMENTS (ID bigint not null generated by default as identity, VERSION integer not null, ORIGINAL_ID bigint, ENROLLMENT_ID bigint not null, DICTIONARY_KEY varchar(900), UNIQUE_NAME varchar(128), displayName varchar(128), PATH varchar(900) not null, PATH_HASH bigint not null, REPARENTED char(1), ACTIVE_FROM bigint not null, ACTIVE_TO bigint not null, primary key (ID))",
                                 "CREATE UNIQUE INDEX DICT_ELEMENTS_UNIQUE ON DICT_ELEMENTS(ORIGINAL_ID, ENROLLMENT_ID, DICTIONARY_KEY, ACTIVE_FROM, ACTIVE_TO)");
        }

        return null;
    }
}
