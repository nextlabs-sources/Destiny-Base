package com.nextlabs.destiny.container.dkms.impl;

import java.util.Collection;
import java.util.Set;

import org.apache.axis2.databinding.types.HexBinary;

import com.nextlabs.destiny.container.dkms.IKey;
import com.nextlabs.destiny.container.dkms.IKeyId;
import com.nextlabs.destiny.container.dkms.IKeyRing;
import com.nextlabs.destiny.container.dkms.KeyManagementException;
import com.nextlabs.destiny.services.keymanagement.types.KeyDTO;
import com.nextlabs.destiny.services.keymanagement.types.KeyIdDTO;
import com.nextlabs.destiny.services.keymanagement.types.KeyRingDTO;
import com.nextlabs.destiny.services.keymanagement.types.KeyRingNamesDTO;
import com.nextlabs.destiny.services.keymanagement.types.KeyRingWithKeysDTO;

public final class DTOConverter {

    public static IKeyId convertToKeyId(KeyIdDTO keyIdDTO){
        return new KeyId(keyIdDTO.getHash().getBytes(), keyIdDTO.getTimestamp());
    }
    
    public static KeyIdDTO convertToKeyIdDTO(IKeyId keyId){
    	KeyIdDTO key = new KeyIdDTO();
        
    	key.setHash(new HexBinary(keyId.getId()));
    	key.setTimestamp(keyId.getCreationTimeStamp());
    	
        return key;
    }
    
    public static KeyRingDTO convertToKeyRingDTO(IKeyRing keyRing) throws KeyManagementException{
    	
    	KeyRingDTO krDTO = new KeyRingDTO();
    	krDTO.setName(keyRing.getName());
    	krDTO.setKeyIds(toKeyIdDTOs(keyRing.getKeys()));
    	krDTO.setCreateDate(keyRing.getKeyRingDO().getCreated().getTime());
    	krDTO.setLastModifiedDate(keyRing.getKeyRingDO().getLastUpdated().getTime() );
    	
        return krDTO;
    }
    
    public static KeyRingWithKeysDTO convertToKeyRingWithKeysDTO(IKeyRing keyRing) throws KeyManagementException {
    	
    	KeyRingWithKeysDTO krDTO = new KeyRingWithKeysDTO();
    	krDTO.setName(keyRing.getName());
    	krDTO.setKeys(toKeyDTOs(keyRing.getKeys()));
    	krDTO.setCreateDate(keyRing.getKeyRingDO().getCreated().getTime());
    	krDTO.setLastModifiedDate(keyRing.getKeyRingDO().getLastUpdated().getTime() );
    	
        return krDTO;
    }
    
    public static KeyIdDTO[] toKeyIdDTOs(Collection<IKey> keys){
        KeyIdDTO[] result = new KeyIdDTO[keys.size()];
        int i =0;
        for(IKey key : keys){
            result[i++] = new KeyIdDTO();
            result[i++].setHash(new HexBinary(key.getId()));
            result[i++].setTimestamp(key.getCreationTimeStamp());
        }
        return result;
    }
    
    public static KeyDTO[] toKeyDTOs(Collection<IKey> keys){
        KeyDTO[] result = new KeyDTO[keys.size()];
        int i =0;
        for(IKey key : keys){
            result[i++] = convertToKeyDTO(key);
        }
        return result;
    }
    
    public static KeyRingNamesDTO toKeyRingNamesDTO(Set<String> keyRingNames) {
        String[] names = keyRingNames.toArray(new String[keyRingNames.size()]);
        KeyRingNamesDTO dto = new KeyRingNamesDTO();
        dto.setKeyRingNames(names);
        
        return dto;
    }
    
    public static IKey convertToKey(KeyDTO keyDTO) {
    	
        return new Key(
                new KeyId(keyDTO.getKeyId().getHash().getBytes(), keyDTO.getKeyId().getTimestamp())
              , keyDTO.getVersion()
              , keyDTO.getKey().getBytes()
        );
    }
    
    public static KeyDTO convertToKeyDTO(IKey key) {
    	
    	KeyIdDTO keyIDDto = new KeyIdDTO();
    	keyIDDto.setHash(new HexBinary(key.getId()));
    	keyIDDto.setTimestamp(key.getCreationTimeStamp());
    	
    	KeyDTO keyDTO = new KeyDTO();
    	keyDTO.setKey(new HexBinary(key.getEncoded()));
    	keyDTO.setKeyId(keyIDDto);
    	keyDTO.setVersion(key.getStructureVersion());
    	
    	return keyDTO;
    }
    
    private DTOConverter() {
    }
}
