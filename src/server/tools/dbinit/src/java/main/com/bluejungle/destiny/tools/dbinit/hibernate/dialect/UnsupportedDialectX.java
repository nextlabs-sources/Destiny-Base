/*
 * Created on Dec 17, 2007
 * 
 * All sources, binaries and HTML pages (C) copyright 2004-2007 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 */
package com.bluejungle.destiny.tools.dbinit.hibernate.dialect;

import java.sql.Connection;

import net.sf.hibernate.dialect.Dialect;

import com.bluejungle.destiny.tools.dbinit.hibernate.mapping.ColumnM;
import com.bluejungle.destiny.tools.dbinit.hibernate.mapping.ConstraintM;

/**
 * Use this class for a dialect that hasn't been extended.
 * This class is useful for installing a new database, as that doesn't use any extended methods
 * 
 * @author hchan
 */
@SuppressWarnings("unused")
public class UnsupportedDialectX extends DialectExtended{
	public UnsupportedDialectX(Dialect dialect) {
		super(dialect);
	}

	@Override
	public String getAlterColumnString() {
		throw new UnsupportedOperationException("getAlterColumnString");
	}

	@Override
	public String getDefaultValue(short dataType) {
		throw new UnsupportedOperationException("getDefaultValue");
	}

	@Override
	public String getDropColumnString() {
		throw new UnsupportedOperationException("getDropColumnString");
	}

	@Override
	public String getSetNullableString(String sameType, boolean setNull) {
		throw new UnsupportedOperationException("getSetNullableString");
	}

	@Override
	public int compareLength(ColumnM dbColumnM, ColumnM hibColumnM) {
		throw new UnsupportedOperationException("compare");
	}

	@Override
	public boolean isSameType(short c1DataType, short c2DataType) {
		throw new UnsupportedOperationException("isSameType");
	}

	@Override
	public boolean isSequenceExist(Connection connection, String name) {
		throw new UnsupportedOperationException("isSequenceExist");
	}

	@Override
	public String sqlAddUnique(String tableName, String name, String columns) {
		throw new UnsupportedOperationException("sqlAddUnique");
	}

	@Override
	public String sqlAlterColumnType(String tableName, String colname, String newType) {
		throw new UnsupportedOperationException("sqlAlterColumnType");
	}

	@Override
	public boolean isTableBlackListed(String tableName) {
		throw new UnsupportedOperationException("isTableBlackListed");
	}

	@Override
	public String getLengthString() {
		throw new UnsupportedOperationException("getLengthString");
	}

    @Override
    public String sqlRebuildIndex(String indexName, String tableName) {
        throw new UnsupportedOperationException("sqlRebuildIndex");
    }

    @Override
    public String getLargeStringDataType(int size) {
        throw new UnsupportedOperationException("getLargeStringDataType");
    }
}
