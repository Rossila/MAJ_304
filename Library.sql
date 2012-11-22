drop table Fine;
drop table Borrowing;
drop table HoldRequest;
drop table BookCopy;
drop table HasSubject;
drop table hasAuthor;
drop table Book;
drop table Borrower;
drop table BorrowerType;

create table BorrowerType
	(type varchar(20) not null PRIMARY KEY,
	bookTimeLimit integer not null );

create table Borrower
	(bid integer not null PRIMARY KEY,
	password varchar(16) not null,
	name varchar(20) not null,
	address	varchar(50),
	phone integer,
	emailAddress varchar(20),
	sinOrStNo integer not null unique,
	expiryDate date not null,
	type varchar(20) not null,
	foreign key(type) references BorrowerType);

create table Book
	(callNumber varchar(20) not null PRIMARY KEY,
	isbn integer not null unique,
	title varchar(20) not null,
	mainAuthor varchar(20) not null,
	publisher varchar(20) not null,
	year integer not null);

create table HasAuthor
	(callNumber varchar(20) not null,
	name varchar(20) not null, 
	PRIMARY KEY(callNumber, name),
	foreign key(callNumber) references Book on delete CASCADE);

create table HasSubject
	(callNumber varchar(20) not null,
	subject varchar(20) not null, 
	PRIMARY KEY(callNumber, subject),
	foreign key(callNumber) references Book on delete CASCADE);

create table BookCopy
	(callNumber varchar(20) not null,
	copyNo integer not null,
	status varchar(10) not null,
	PRIMARY KEY(callNumber, copyNo),
	foreign key(callNumber) references Book on delete CASCADE);

create table HoldRequest
	(hid integer not null PRIMARY KEY,
	bid integer not null,
	callNumber varchar(20) not null,
	issuedDate date not null,
    foreign key(bid) references Borrower,
    foreign key(callNumber) references Book on delete CASCADE);

create table Borrowing
	(borid integer not null PRIMARY KEY,
	bid integer not null,
	callNumber varchar(20) not null,
	copyNo integer not null,
	outDate date not null,
	inDate date,
	foreign key (bid) references Borrower,
	foreign key (callNumber) references Book on delete CASCADE,
	foreign key (callNumber, copyNo) references BookCopy on delete CASCADE);

create table Fine
	(fid integer not null PRIMARY KEY,
	amount integer not null,
	issuedDate date not null,
	paidDate date,
	borid integer not null,
	foreign key (borid) references Borrowing);

alter table BookCopy add check (status in ('in', 'out', 'on-hold'));

insert into BorrowerType values
	('student', 2);
insert into BorrowerType values
	('faculty', 12);
insert into BorrowerType values
	('staff', 6);

