SET IDENTITY_INSERT RPA_LOG_MAPPING ON;
INSERT INTO RPA_LOG_MAPPING (ID,NAME,DATA_TYPE,ATTR_TYPE,MAPPED_COLUMN,IS_DYNAMIC_ATTR) VALUES (1,'DATE','TIMESTAMP','OTHERS','TIME', '0');
INSERT INTO RPA_LOG_MAPPING (ID,NAME,DATA_TYPE,ATTR_TYPE,MAPPED_COLUMN,IS_DYNAMIC_ATTR) VALUES (2,'USER_NAME','STRING','USER','USER_NAME', '0');
INSERT INTO RPA_LOG_MAPPING (ID,NAME,DATA_TYPE,ATTR_TYPE,MAPPED_COLUMN,IS_DYNAMIC_ATTR) VALUES (3,'ACTION','STRING','OTHERS','ACTION', '0');
INSERT INTO RPA_LOG_MAPPING (ID,NAME,DATA_TYPE,ATTR_TYPE,MAPPED_COLUMN,IS_DYNAMIC_ATTR) VALUES (4,'LOG_LEVEL','STRING','OTHERS','LOG_LEVEL', '0');
INSERT INTO RPA_LOG_MAPPING (ID,NAME,DATA_TYPE,ATTR_TYPE,MAPPED_COLUMN,IS_DYNAMIC_ATTR) VALUES (5,'APPLICATION_NAME','STRING','OTHERS','APPLICATION_NAME', '0');
INSERT INTO RPA_LOG_MAPPING (ID,NAME,DATA_TYPE,ATTR_TYPE,MAPPED_COLUMN,IS_DYNAMIC_ATTR) VALUES (6,'HOST_NAME','STRING','OTHERS','HOST_NAME', '0');
INSERT INTO RPA_LOG_MAPPING (ID,NAME,DATA_TYPE,ATTR_TYPE,MAPPED_COLUMN,IS_DYNAMIC_ATTR) VALUES (7,'HOST_IP','STRING','OTHERS','HOST_IP', '0');
INSERT INTO RPA_LOG_MAPPING (ID,NAME,DATA_TYPE,ATTR_TYPE,MAPPED_COLUMN,IS_DYNAMIC_ATTR) VALUES (8,'POLICY_NAME','STRING','POLICY','POLICY_NAME', '0');
INSERT INTO RPA_LOG_MAPPING (ID,NAME,DATA_TYPE,ATTR_TYPE,MAPPED_COLUMN,IS_DYNAMIC_ATTR) VALUES (9,'POLICY_FULLNAME','STRING','POLICY','POLICY_FULLNAME', '0');
INSERT INTO RPA_LOG_MAPPING (ID,NAME,DATA_TYPE,ATTR_TYPE,MAPPED_COLUMN,IS_DYNAMIC_ATTR) VALUES (10,'POLICY_DECISION','STRING','POLICY','POLICY_DECISION', '0');
INSERT INTO RPA_LOG_MAPPING (ID,NAME,DATA_TYPE,ATTR_TYPE,MAPPED_COLUMN,IS_DYNAMIC_ATTR) VALUES (11,'FROM_RESOURCE_NAME','STRING','RESOURCE','FROM_RESOURCE_NAME', '0');
INSERT INTO RPA_LOG_MAPPING (ID,NAME,DATA_TYPE,ATTR_TYPE,MAPPED_COLUMN,IS_DYNAMIC_ATTR) VALUES (12,'FROM_RESOURCE_PATH','STRING','RESOURCE','FROM_RESOURCE_PATH', '0');
INSERT INTO RPA_LOG_MAPPING (ID,NAME,DATA_TYPE,ATTR_TYPE,MAPPED_COLUMN,IS_DYNAMIC_ATTR) VALUES (13,'TO_RESOURCE_NAME','STRING','RESOURCE','TO_RESOURCE_NAME', '0');
SET IDENTITY_INSERT RPA_LOG_MAPPING OFF;

SET IDENTITY_INSERT SAVED_REPORTS ON;
INSERT INTO SAVED_REPORTS (ID,DESCRIPTION,TITLE,IS_SHARED,CRITERIA_JSON,CREATED_DATE,LAST_UPDATED_DATE,IS_DELETED,IS_IN_DASHBOARD,OWNER_ID) values (1,'Top 10 Deny Policies in Last 30 Days v1.0 - [Dashboard]','Top 10 Deny Policies in Last 30 Days','0','{ "filters" :{  "general" : { "type" : "report",  "date_mode" : "Relative",  "window_mode" : "last_30_days",  "start_date" : "null",  "end_date" : "null",  "fields" : [    { "name": "log_level", "operator": "eq", "value": "3", "has_multi_value" : "false", "function" : ""},     { "name": "decision", "operator": "eq", "value" : "D", "has_multi_value" : "false", "function" : ""},     { "name": "action", "operator": "in", "value" : [], "has_multi_value" : "true", "function" : ""}    ] },  "user_criteria" : { "look_up_field" : { "name" : "user_id", "operator" : "in", "value": [""], "has_multi_value" : "true", "function" : "" } , "fields" : []  }, "resource_criteria" : { "look_up_field" : { "name" : "resource_path", "operator" : "in", "value": [""], "has_multi_value" : "true", "function" : "" } , "fields" : []  }, "policy_criteria" : { "look_up_field" : { "name" : "policy_name", "operator" : "in", "value": [""], "has_multi_value" : "true", "function" : "" } , "fields" : []  }, "other_criteria" : { "look_up_field" : { "name" : "", "operator" : "", "value": "", "has_multi_value" : "false", "function" : "" } , "fields" : []  } }, "header": ["usr_USER_NAME","plc_POLICY_FULLNAME","plc_POLICY_DECISION","oth_HOST_NAME","oth_APPLICATION_NAME"],"order_by" : [ { "col_name" : "ResultCount", "sort_order" : "desc"} ],"max_rows" : "10" ,"grouping_mode" : "GROUP_BY_POLICY" ,"aggregators" : [],"group_by" : [], "save_info" : {  "report_name" : "Top 10 Deny Policies in Last 30 Days",  "report_desc" : "Top 10 Deny Policies in Last 30 Days v1.0 - [Dashboard]",  "report_type" : "PIE_CHART"}}', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,'0','1',0);
INSERT INTO SAVED_REPORTS (ID,DESCRIPTION,TITLE,IS_SHARED,CRITERIA_JSON,CREATED_DATE,LAST_UPDATED_DATE,IS_DELETED,IS_IN_DASHBOARD,OWNER_ID) values (2,'Top 5 Denied Resource in Last 7 Days v1.0 - [Dashboard]','Top 5 Denied Resource in Last 7 Days','0','{ "filters" :{  "general" : { "type" : "report",  "date_mode" : "Relative",  "window_mode" : "last_7_days",  "start_date" : "null",  "end_date" : "null",  "fields" : [    { "name": "log_level", "operator": "eq", "value": "3", "has_multi_value" : "false", "function" : ""},     { "name": "decision", "operator": "eq", "value" : "D", "has_multi_value" : "false", "function" : ""},     { "name": "action", "operator": "in", "value" : [], "has_multi_value" : "true", "function" : ""}    ] },  "user_criteria" : { "look_up_field" : { "name" : "user_id", "operator" : "in", "value": [""], "has_multi_value" : "true", "function" : "" } , "fields" : []  }, "resource_criteria" : { "look_up_field" : { "name" : "resource_path", "operator" : "in", "value": [""], "has_multi_value" : "true", "function" : "" } , "fields" : []  }, "policy_criteria" : { "look_up_field" : { "name" : "policy_name", "operator" : "in", "value": [""], "has_multi_value" : "true", "function" : "" } , "fields" : []  }, "other_criteria" : { "look_up_field" : { "name" : "", "operator" : "", "value": "", "has_multi_value" : "false", "function" : "" } , "fields" : []  } }, "header": ["usr_USER_NAME","plc_POLICY_FULLNAME","plc_POLICY_DECISION","oth_HOST_NAME","oth_APPLICATION_NAME"],"order_by" : [ { "col_name" : "ResultCount", "sort_order" : "desc"} ],"max_rows" : "5" ,"grouping_mode" : "GROUP_BY_RESOURCE" ,"aggregators" : [],"group_by" : [], "save_info" : {  "report_name" : "Top 5 Denied Resource in Last 7 Days",  "report_desc" : "Top 5 Denied Resource in Last 7 Days v1.0 - [Dashboard]",  "report_type" : "HORZ_BAR_CHART"}}', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,'0','1',0);
INSERT INTO SAVED_REPORTS (ID,DESCRIPTION,TITLE,IS_SHARED,CRITERIA_JSON,CREATED_DATE,LAST_UPDATED_DATE,IS_DELETED,IS_IN_DASHBOARD,OWNER_ID) values (3,'Top 5 Allow Resource in Last 7 Days v1.0 - [Dashboard]','Top 5 Allow Resource in Last 7 Days','0','{ "filters" :{  "general" : { "type" : "report",  "date_mode" : "Relative",  "window_mode" : "last_7_days",  "start_date" : "null",  "end_date" : "null",  "fields" : [    { "name": "log_level", "operator": "eq", "value": "3", "has_multi_value" : "false", "function" : ""},     { "name": "decision", "operator": "eq", "value" : "A", "has_multi_value" : "false", "function" : ""},     { "name": "action", "operator": "in", "value" : [], "has_multi_value" : "true", "function" : ""}    ] },  "user_criteria" : { "look_up_field" : { "name" : "user_id", "operator" : "in", "value": [""], "has_multi_value" : "true", "function" : "" } , "fields" : []  }, "resource_criteria" : { "look_up_field" : { "name" : "resource_path", "operator" : "in", "value": [""], "has_multi_value" : "true", "function" : "" } , "fields" : []  }, "policy_criteria" : { "look_up_field" : { "name" : "policy_name", "operator" : "in", "value": [""], "has_multi_value" : "true", "function" : "" } , "fields" : []  }, "other_criteria" : { "look_up_field" : { "name" : "", "operator" : "", "value": "", "has_multi_value" : "false", "function" : "" } , "fields" : []  } }, "header": ["usr_USER_NAME","plc_POLICY_FULLNAME","plc_POLICY_DECISION","oth_HOST_NAME","oth_APPLICATION_NAME"],"order_by" : [ { "col_name" : "ResultCount", "sort_order" : "desc"} ],"max_rows" : "5" ,"grouping_mode" : "GROUP_BY_RESOURCE" ,"aggregators" : [],"group_by" : [], "save_info" : {  "report_name" : "Top 5 Allow Resource in Last 7 Days",  "report_desc" : "Top 5 Allow Resource in Last 7 Days v1.0 - [Dashboard]",  "report_type" : "HORZ_BAR_CHART"}}', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,'0','1',0);
INSERT INTO SAVED_REPORTS (ID,DESCRIPTION,TITLE,IS_SHARED,CRITERIA_JSON,CREATED_DATE,LAST_UPDATED_DATE,IS_DELETED,IS_IN_DASHBOARD,OWNER_ID) values (4,'Trend of Deny Policies in Last 30 Days v1.0 - [Dashboard]','Trend of Deny Policies in Last 30 Days','0','{ "filters" :{  "general" : { "type" : "report",  "date_mode" : "Relative",  "window_mode" : "last_30_days",  "start_date" : "null",  "end_date" : "null",  "fields" : [    { "name": "log_level", "operator": "eq", "value": "3", "has_multi_value" : "false", "function" : ""},     { "name": "decision", "operator": "eq", "value" : "D", "has_multi_value" : "false", "function" : ""},     { "name": "action", "operator": "in", "value" : [], "has_multi_value" : "true", "function" : ""}    ] },  "user_criteria" : { "look_up_field" : { "name" : "user_id", "operator" : "in", "value": [""], "has_multi_value" : "true", "function" : "" } , "fields" : []  }, "resource_criteria" : { "look_up_field" : { "name" : "resource_path", "operator" : "in", "value": [""], "has_multi_value" : "true", "function" : "" } , "fields" : []  }, "policy_criteria" : { "look_up_field" : { "name" : "policy_name", "operator" : "in", "value": [""], "has_multi_value" : "true", "function" : "" } , "fields" : []  }, "other_criteria" : { "look_up_field" : { "name" : "", "operator" : "", "value": "", "has_multi_value" : "false", "function" : "" } , "fields" : []  } }, "header": ["usr_USER_NAME","plc_POLICY_FULLNAME","plc_POLICY_DECISION","oth_HOST_NAME","oth_APPLICATION_NAME"],"order_by" : [ { "col_name" : "day_nb", "sort_order" : "asc"} ],"max_rows" : "100" ,"grouping_mode" : "GROUP_BY_DAY" ,"aggregators" : [],"group_by" : [], "save_info" : {  "report_name" : "Trend of Deny Policies in Last 30 Days",  "report_desc" : "Trend of Deny Policies in Last 30 Days v1.0 - [Dashboard]",  "report_type" : "BAR_CHART"}}', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,'0','1',0);
INSERT INTO SAVED_REPORTS (ID,DESCRIPTION,TITLE,IS_SHARED,CRITERIA_JSON,CREATED_DATE,LAST_UPDATED_DATE,IS_DELETED,IS_IN_DASHBOARD,OWNER_ID) values (5,'Top 10 Denied Users in Last 30 Days v1.0 - [Dashboard]','Top 10 Denied Users in Last 30 Days','0','{ "filters" :{  "general" : { "type" : "report",  "date_mode" : "Relative",  "window_mode" : "last_30_days",  "start_date" : "null",  "end_date" : "null",  "fields" : [    { "name": "log_level", "operator": "eq", "value": "3", "has_multi_value" : "false", "function" : ""},     { "name": "decision", "operator": "eq", "value" : "D", "has_multi_value" : "false", "function" : ""},     { "name": "action", "operator": "in", "value" : [], "has_multi_value" : "true", "function" : ""}    ] },  "user_criteria" : { "look_up_field" : { "name" : "user_id", "operator" : "in", "value": [""], "has_multi_value" : "true", "function" : "" } , "fields" : []  }, "resource_criteria" : { "look_up_field" : { "name" : "resource_path", "operator" : "in", "value": [""], "has_multi_value" : "true", "function" : "" } , "fields" : []  }, "policy_criteria" : { "look_up_field" : { "name" : "policy_name", "operator" : "in", "value": [""], "has_multi_value" : "true", "function" : "" } , "fields" : []  }, "other_criteria" : { "look_up_field" : { "name" : "", "operator" : "", "value": "", "has_multi_value" : "false", "function" : "" } , "fields" : []  } }, "header": ["usr_USER_NAME","plc_POLICY_FULLNAME","plc_POLICY_DECISION","oth_HOST_NAME","oth_APPLICATION_NAME"],"order_by" : [ { "col_name" : "ResultCount", "sort_order" : "desc"} ],"max_rows" : "10" ,"grouping_mode" : "GROUP_BY_USER" ,"aggregators" : [],"group_by" : [], "save_info" : {  "report_name" : "Top 10 Denied Users in Last 30 Days",  "report_desc" : "Top 10 Denied Users in Last 30 Days v1.0 - [Dashboard]",  "report_type" : "BAR_CHART"}}', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,'0','1',0);
INSERT INTO SAVED_REPORTS (ID,DESCRIPTION,TITLE,IS_SHARED,CRITERIA_JSON,CREATED_DATE,LAST_UPDATED_DATE,IS_DELETED,IS_IN_DASHBOARD,OWNER_ID) values (6,'Last 10 Deny Enforcement in Last 7 Days v1.0 - [Dashboard]','Last 10 Deny Enforcement in Last 7 Days','0','{ "filters" :{  "general" : { "type" : "report",  "date_mode" : "Relative",  "window_mode" : "last_7_days",  "start_date" : "null",  "end_date" : "null",  "fields" : [    { "name": "log_level", "operator": "eq", "value": "3", "has_multi_value" : "false", "function" : ""},     { "name": "decision", "operator": "eq", "value" : "D", "has_multi_value" : "false", "function" : ""},     { "name": "action", "operator": "in", "value" : [], "has_multi_value" : "true", "function" : ""}    ] },  "user_criteria" : { "look_up_field" : { "name" : "user_id", "operator" : "in", "value": [""], "has_multi_value" : "true", "function" : "" } , "fields" : []  }, "resource_criteria" : { "look_up_field" : { "name" : "resource_path", "operator" : "in", "value": [""], "has_multi_value" : "true", "function" : "" } , "fields" : []  }, "policy_criteria" : { "look_up_field" : { "name" : "policy_name", "operator" : "in", "value": [""], "has_multi_value" : "true", "function" : "" } , "fields" : []  }, "other_criteria" : { "look_up_field" : { "name" : "", "operator" : "", "value": "", "has_multi_value" : "false", "function" : "" } , "fields" : []  } }, "header": ["usr_USER_NAME","res_FROM_RESOURCE_NAME","plc_POLICY_DECISION","oth_ACTION"],"order_by" : [ { "col_name" : "oth_DATE", "sort_order" : "desc"} ],"max_rows" : "10" ,"grouping_mode" : "" ,"aggregators" : [],"group_by" : [], "save_info" : {  "report_name" : "Last 10 Deny Enforcement in Last 7 Days",  "report_desc" : "Last 10 Deny Enforcement in Last 7 Days v1.0 - [Dashboard]",  "report_type" : "TABULAR"}}', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,'0','1',0);
INSERT INTO SAVED_REPORTS (ID,DESCRIPTION,TITLE,IS_SHARED,CRITERIA_JSON,CREATED_DATE,LAST_UPDATED_DATE,IS_DELETED,IS_IN_DASHBOARD,OWNER_ID) values (7,'Last 10 Allow Enforcement in Last 7 Days v1.0 - [Dashboard]','Last 10 Allow Enforcement in Last 7 Days','0','{ "filters" :{  "general" : { "type" : "report",  "date_mode" : "Relative",  "window_mode" : "last_7_days",  "start_date" : "null",  "end_date" : "null",  "fields" : [    { "name": "log_level", "operator": "eq", "value": "3", "has_multi_value" : "false", "function" : ""},     { "name": "decision", "operator": "eq", "value" : "A", "has_multi_value" : "false", "function" : ""},     { "name": "action", "operator": "in", "value" : [], "has_multi_value" : "true", "function" : ""}    ] },  "user_criteria" : { "look_up_field" : { "name" : "user_id", "operator" : "in", "value": [""], "has_multi_value" : "true", "function" : "" } , "fields" : []  }, "resource_criteria" : { "look_up_field" : { "name" : "resource_path", "operator" : "in", "value": [""], "has_multi_value" : "true", "function" : "" } , "fields" : []  }, "policy_criteria" : { "look_up_field" : { "name" : "policy_name", "operator" : "in", "value": [""], "has_multi_value" : "true", "function" : "" } , "fields" : []  }, "other_criteria" : { "look_up_field" : { "name" : "", "operator" : "", "value": "", "has_multi_value" : "false", "function" : "" } , "fields" : []  } }, "header": ["usr_USER_NAME","res_FROM_RESOURCE_NAME","plc_POLICY_DECISION","oth_ACTION"],"order_by" : [ { "col_name" : "oth_DATE", "sort_order" : "desc"} ],"max_rows" : "10" ,"grouping_mode" : "" ,"aggregators" : [],"group_by" : [], "save_info" : {  "report_name" : "Last 10 Allow Enforcement in Last 7 Days",  "report_desc" : "Last 10 Allow Enforcement in Last 7 Days v1.0 - [Dashboard]",  "report_type" : "TABULAR"}}', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,'0','1',0);
INSERT INTO SAVED_REPORTS (ID,DESCRIPTION,TITLE,IS_SHARED,CRITERIA_JSON,CREATED_DATE,LAST_UPDATED_DATE,IS_DELETED,IS_IN_DASHBOARD,OWNER_ID) VALUES (8,'Allow Enforcement in Last 7 Days v1.0','Allow Enforcement in Last 7 Days','1','{ "filters" :{  "general" : { "type" : "report",  "date_mode" : "Relative",  "window_mode" : "last_7_days",  "start_date" : "null",  "end_date" : "null",  "fields" : [    { "name": "log_level", "operator": "eq", "value": "3", "has_multi_value" : "false", "function" : ""},     { "name": "decision", "operator": "eq", "value" : "A", "has_multi_value" : "false", "function" : ""},     { "name": "action", "operator": "in", "value" : [], "has_multi_value" : "true", "function" : ""}    ] },  "user_criteria" : { "look_up_field" : { "name" : "user_id", "operator" : "in", "value": [""], "has_multi_value" : "true", "function" : "" } , "fields" : []  }, "resource_criteria" : { "look_up_field" : { "name" : "resource_path", "operator" : "in", "value": [""], "has_multi_value" : "true", "function" : "" } , "fields" : []  }, "policy_criteria" : { "look_up_field" : { "name" : "policy_name", "operator" : "in", "value": [""], "has_multi_value" : "true", "function" : "" } , "fields" : []  }, "other_criteria" : { "look_up_field" : { "name" : "", "operator" : "", "value": "", "has_multi_value" : "false", "function" : "" } , "fields" : []  } }, "header": ["usr_USER_NAME","res_FROM_RESOURCE_NAME","plc_POLICY_FULLNAME","plc_POLICY_DECISION","oth_ACTION"],"order_by" : [ { "col_name" : "oth_DATE", "sort_order" : "desc"} ],"max_rows" : "200" ,"grouping_mode" : "" ,"aggregators" : [],"group_by" : [], "save_info" : {  "report_name" : "Allow Enforcement in Last 7 Days",  "report_desc" : "Allow Enforcement in Last 7 Days v1.0",  "report_type" : "TABULAR"}}', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,'0','0',0);
INSERT INTO SAVED_REPORTS (ID,DESCRIPTION,TITLE,IS_SHARED,CRITERIA_JSON,CREATED_DATE,LAST_UPDATED_DATE,IS_DELETED,IS_IN_DASHBOARD,OWNER_ID) VALUES (9,'Deny Enforcement in Last 7 Days v1.0','Deny Enforcement in Last 7 Days','1','{ "filters" :{  "general" : { "type" : "report",  "date_mode" : "Relative",  "window_mode" : "last_7_days",  "start_date" : "null",  "end_date" : "null",  "fields" : [    { "name": "log_level", "operator": "eq", "value": "3", "has_multi_value" : "false", "function" : ""},     { "name": "decision", "operator": "eq", "value" : "D", "has_multi_value" : "false", "function" : ""},     { "name": "action", "operator": "in", "value" : [], "has_multi_value" : "true", "function" : ""}    ] },  "user_criteria" : { "look_up_field" : { "name" : "user_id", "operator" : "in", "value": [""], "has_multi_value" : "true", "function" : "" } , "fields" : []  }, "resource_criteria" : { "look_up_field" : { "name" : "resource_path", "operator" : "in", "value": [""], "has_multi_value" : "true", "function" : "" } , "fields" : []  }, "policy_criteria" : { "look_up_field" : { "name" : "policy_name", "operator" : "in", "value": [""], "has_multi_value" : "true", "function" : "" } , "fields" : []  }, "other_criteria" : { "look_up_field" : { "name" : "", "operator" : "", "value": "", "has_multi_value" : "false", "function" : "" } , "fields" : []  } }, "header": ["usr_USER_NAME","res_FROM_RESOURCE_NAME","plc_POLICY_FULLNAME","plc_POLICY_DECISION","oth_ACTION"],"order_by" : [ { "col_name" : "oth_DATE", "sort_order" : "desc"} ],"max_rows" : "200" ,"grouping_mode" : "" ,"aggregators" : [],"group_by" : [], "save_info" : {  "report_name" : "Deny Enforcement in Last 7 Days",  "report_desc" : "Deny Enforcement in Last 7 Days v1.0",  "report_type" : "TABULAR"}}', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,'0','0',0);
INSERT INTO SAVED_REPORTS (ID,DESCRIPTION,TITLE,IS_SHARED,CRITERIA_JSON,CREATED_DATE,LAST_UPDATED_DATE,IS_DELETED,IS_IN_DASHBOARD,OWNER_ID) VALUES (10,'Denied Users in Last 30 Days v1.0','Denied Users in Last 30 Days','1','{ "filters" :{  "general" : { "type" : "report",  "date_mode" : "Relative",  "window_mode" : "last_30_days",  "start_date" : "null",  "end_date" : "null",  "fields" : [    { "name": "log_level", "operator": "eq", "value": "3", "has_multi_value" : "false", "function" : ""},     { "name": "decision", "operator": "eq", "value" : "D", "has_multi_value" : "false", "function" : ""},     { "name": "action", "operator": "in", "value" : [], "has_multi_value" : "true", "function" : ""}    ] },  "user_criteria" : { "look_up_field" : { "name" : "user_id", "operator" : "in", "value": [""], "has_multi_value" : "true", "function" : "" } , "fields" : []  }, "resource_criteria" : { "look_up_field" : { "name" : "resource_path", "operator" : "in", "value": [""], "has_multi_value" : "true", "function" : "" } , "fields" : []  }, "policy_criteria" : { "look_up_field" : { "name" : "policy_name", "operator" : "in", "value": [""], "has_multi_value" : "true", "function" : "" } , "fields" : []  }, "other_criteria" : { "look_up_field" : { "name" : "", "operator" : "", "value": "", "has_multi_value" : "false", "function" : "" } , "fields" : []  } }, "header": ["usr_USER_NAME","plc_POLICY_FULLNAME","plc_POLICY_DECISION","oth_HOST_NAME","oth_APPLICATION_NAME"],"order_by" : [ { "col_name" : "ResultCount", "sort_order" : "desc"} ],"max_rows" : "50" ,"grouping_mode" : "GROUP_BY_USER" ,"aggregators" : [],"group_by" : [], "save_info" : {  "report_name" : "Denied Users in Last 30 Days",  "report_desc" : "Denied Users in Last 30 Days v1.0",  "report_type" : "BAR_CHART"}}', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,'0','0',0);
INSERT INTO SAVED_REPORTS (ID,DESCRIPTION,TITLE,IS_SHARED,CRITERIA_JSON,CREATED_DATE,LAST_UPDATED_DATE,IS_DELETED,IS_IN_DASHBOARD,OWNER_ID) VALUES (11,'Deny Policies in Last 30 Days v1.0','Deny Policies in Last 30 Days','1','{ "filters" :{  "general" : { "type" : "report",  "date_mode" : "Relative",  "window_mode" : "last_30_days",  "start_date" : "null",  "end_date" : "null",  "fields" : [    { "name": "log_level", "operator": "eq", "value": "3", "has_multi_value" : "false", "function" : ""},     { "name": "decision", "operator": "eq", "value" : "D", "has_multi_value" : "false", "function" : ""},     { "name": "action", "operator": "in", "value" : [], "has_multi_value" : "true", "function" : ""}    ] },  "user_criteria" : { "look_up_field" : { "name" : "user_id", "operator" : "in", "value": [""], "has_multi_value" : "true", "function" : "" } , "fields" : []  }, "resource_criteria" : { "look_up_field" : { "name" : "resource_path", "operator" : "in", "value": [""], "has_multi_value" : "true", "function" : "" } , "fields" : []  }, "policy_criteria" : { "look_up_field" : { "name" : "policy_name", "operator" : "in", "value": [""], "has_multi_value" : "true", "function" : "" } , "fields" : []  }, "other_criteria" : { "look_up_field" : { "name" : "", "operator" : "", "value": "", "has_multi_value" : "false", "function" : "" } , "fields" : []  } }, "header": ["usr_USER_NAME","plc_POLICY_FULLNAME","plc_POLICY_DECISION","oth_HOST_NAME","oth_APPLICATION_NAME"],"order_by" : [ { "col_name" : "ResultCount", "sort_order" : "desc"} ],"max_rows" : "50" ,"grouping_mode" : "GROUP_BY_POLICY" ,"aggregators" : [],"group_by" : [], "save_info" : {  "report_name" : "Deny Policies in Last 30 Days",  "report_desc" : "Deny Policies in Last 30 Days v1.0",  "report_type" : "PIE_CHART"}}', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,'0','0',0);
INSERT INTO SAVED_REPORTS (ID,DESCRIPTION,TITLE,IS_SHARED,CRITERIA_JSON,CREATED_DATE,LAST_UPDATED_DATE,IS_DELETED,IS_IN_DASHBOARD,OWNER_ID) VALUES (12,'Allow Resource in Last 7 Days v1.0','Allow Resource in Last 7 Days','1','{ "filters" :{  "general" : { "type" : "report",  "date_mode" : "Relative",  "window_mode" : "last_7_days",  "start_date" : "null",  "end_date" : "null",  "fields" : [    { "name": "log_level", "operator": "eq", "value": "3", "has_multi_value" : "false", "function" : ""},     { "name": "decision", "operator": "eq", "value" : "A", "has_multi_value" : "false", "function" : ""},     { "name": "action", "operator": "in", "value" : [], "has_multi_value" : "true", "function" : ""}    ] },  "user_criteria" : { "look_up_field" : { "name" : "user_id", "operator" : "in", "value": [""], "has_multi_value" : "true", "function" : "" } , "fields" : []  }, "resource_criteria" : { "look_up_field" : { "name" : "resource_path", "operator" : "in", "value": [""], "has_multi_value" : "true", "function" : "" } , "fields" : []  }, "policy_criteria" : { "look_up_field" : { "name" : "policy_name", "operator" : "in", "value": [""], "has_multi_value" : "true", "function" : "" } , "fields" : []  }, "other_criteria" : { "look_up_field" : { "name" : "", "operator" : "", "value": "", "has_multi_value" : "false", "function" : "" } , "fields" : []  } }, "header": ["usr_USER_NAME","plc_POLICY_FULLNAME","plc_POLICY_DECISION","oth_HOST_NAME","oth_APPLICATION_NAME"],"order_by" : [ { "col_name" : "ResultCount", "sort_order" : "desc"} ],"max_rows" : "50" ,"grouping_mode" : "GROUP_BY_RESOURCE" ,"aggregators" : [],"group_by" : [], "save_info" : {  "report_name" : "Allow Resource in Last 7 Days",  "report_desc" : "Allow Resource in Last 7 Days v1.0",  "report_type" : "HORZ_BAR_CHART"}}', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,'0','0',0);
INSERT INTO SAVED_REPORTS (ID,DESCRIPTION,TITLE,IS_SHARED,CRITERIA_JSON,CREATED_DATE,LAST_UPDATED_DATE,IS_DELETED,IS_IN_DASHBOARD,OWNER_ID) VALUES (14,'Denied Resource in Last 7 Days v1.0','Denied Resource in Last 7 Days','1','{ "filters" :{  "general" : { "type" : "report",  "date_mode" : "Relative",  "window_mode" : "last_7_days",  "start_date" : "null",  "end_date" : "null",  "fields" : [    { "name": "log_level", "operator": "eq", "value": "3", "has_multi_value" : "false", "function" : ""},     { "name": "decision", "operator": "eq", "value" : "D", "has_multi_value" : "false", "function" : ""},     { "name": "action", "operator": "in", "value" : [], "has_multi_value" : "true", "function" : ""}    ] },  "user_criteria" : { "look_up_field" : { "name" : "user_id", "operator" : "in", "value": [""], "has_multi_value" : "true", "function" : "" } , "fields" : []  }, "resource_criteria" : { "look_up_field" : { "name" : "resource_path", "operator" : "in", "value": [""], "has_multi_value" : "true", "function" : "" } , "fields" : []  }, "policy_criteria" : { "look_up_field" : { "name" : "policy_name", "operator" : "in", "value": [""], "has_multi_value" : "true", "function" : "" } , "fields" : []  }, "other_criteria" : { "look_up_field" : { "name" : "", "operator" : "", "value": "", "has_multi_value" : "false", "function" : "" } , "fields" : []  } }, "header": ["usr_USER_NAME","plc_POLICY_FULLNAME","plc_POLICY_DECISION","oth_HOST_NAME","oth_APPLICATION_NAME"],"order_by" : [ { "col_name" : "ResultCount", "sort_order" : "desc"} ],"max_rows" : "50" ,"grouping_mode" : "GROUP_BY_RESOURCE" ,"aggregators" : [],"group_by" : [], "save_info" : {  "report_name" : "Denied Resource in Last 7 Days",  "report_desc" : "Denied Resource in Last 7 Days v1.0",  "report_type" : "HORZ_BAR_CHART"}}', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,'0','0',0);
SET IDENTITY_INSERT SAVED_REPORTS OFF;