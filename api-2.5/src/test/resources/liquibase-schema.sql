CREATE TABLE LIQUIBASECHANGELOG
(
    ID            varchar(255) NOT NULL,
    AUTHOR        varchar(255) NOT NULL,
    FILENAME      varchar(255) NOT NULL,
    DATEEXECUTED  datetime     NOT NULL,
    ORDEREXECUTED int(11)      NOT NULL,
    EXECTYPE      varchar(10)  NOT NULL,
    MD5SUM        varchar(35),
    DESCRIPTION   varchar(255),
    COMMENTS      varchar(255),
    TAG           varchar(255),
    LIQUIBASE     varchar(20),
    CONTEXTS      varchar(255),
    LABELS        varchar(255),
    DEPLOYMENT_ID varchar(10),
    PRIMARY KEY (ID, AUTHOR, FILENAME)
);

CREATE TABLE LIQUIBASECHANGELOGLOCK
(
    ID          int(11)    NOT NULL,
    LOCKED      tinyint(1) NOT NULL,
    LOCKGRANTED datetime,
    LOCKEDBY    varchar(255),
    PRIMARY KEY (ID)
);