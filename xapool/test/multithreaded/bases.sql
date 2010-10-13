create table hproduct("NAME" CHAR(10) NOT NULL ,
                      "CREATEDATE" TIMESTAMP NOT NULL ,
                      "UPDATEDATE" TIMESTAMP NOT NULL );

create table horder("ORDERNUM" CHAR(10) NOT NULL ,
		    "CUSTOMER" CHAR(10) NOT NULL ,
		    "CREATEDATE" TIMESTAMP NOT NULL ,
		    "UPDATEDATE" TIMESTAMP NOT NULL );
