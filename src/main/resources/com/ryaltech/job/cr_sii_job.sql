CALL SP_DROPATABLE ('JOB_DEFINITION');

create table JOB_DEFINITION
(JOB_NAME_CD             VARCHAR2(1000),
 ENABLED_FLG             VARCHAR2(1)  DEFAULT 'Y'
CONSTRAINT XPK_JOB_DEFINITION PRIMARY KEY (JOB_NAME_CD)
);
COMMENT ON TABLE  JOB_DEFINITION IS 'Synchronization table to prevent multiple instances of the same job to run on multiple nodes at the same time.';   
COMMENT ON COLUMN JOB_DEFINITION.JOB_NAME_CD IS 'Job name code. Primary key.';
COMMENT ON COLUMN JOB_DEFINITION.ENABLED_FLG IS 'Flag to indicate whether job is enabled.';
