CREATE TABLE testdata
(
	id	NUMERIC(9,0) IDENTITY NOT NULL PRIMARY KEY,
	foo	INTEGER NOT NULL
)
go

insert into testdata (foo) values(1)
go

