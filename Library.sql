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
	emailAddress varchar(20) not null,
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
drop sequence bid_counter;
create sequence bid_counter
	start with 1
	increment by 1;
drop sequence borid_counter;
create sequence borid_counter
	start with 1
	increment by 1;
drop sequence hid_counter;
create sequence hid_counter
	start with 1
	increment by 1;
drop sequence fid_counter;
create sequence fid_counter
	start with 1
	increment by 1;

insert into borrower values 
	(bid_counter.nextval, 'password', 'Amanda', '111 amanda street', 123456, 'amanda@amanda.com', 654321, '01-02-03', 'student');
insert into borrower values 
	(bid_counter.nextval, 'password', 'Julien', '222 jules street', 234239, 'julien@julien.com', 324232, '01-02-03', 'faculty');
insert into borrower values 
	(bid_counter.nextval, 'password', 'Marie', '333 marie street', 981235, 'marie@marie.com', 394987, '01-02-03', 'staff');
insert into borrower values 
	(bid_counter.nextval, 'password', 'Harry', null, 910293, 'harry@potter.com', 019375, '01-02-03', 'student');
insert into borrower values 
	(bid_counter.nextval, 'password', 'Hermione', null, null, 'Hermy@granger.com', 123468, '02-03-04', 'faculty');
insert into borrower values 
	(bid_counter.nextval, 'password', 'Ron', '324 end street', null, 'ron@weasley.com', 864135, '03-04-05', 'staff');
insert into borrower values 
	(bid_counter.nextval, 'password', 'Sheep', '123 dream street', null, 'count@sheep.com', 352132, '03-04-05', 'student');

insert into book values 
	('a12345', 12345, 'Peaches', 'Ms Peach', 'Peach Books', 1991);
insert into book values 
	('a23456', 23456, 'Apples', 'Mr Apple', 'Apple Books', 1992);
insert into book values 
	('b34567', 23489, 'Bananas', 'Ms Banana', 'Banana Books', 1993);
insert into book values 
	('c12235', 23432, 'Oranges', 'Ms Peach', 'Peach Books', 1991);
insert into book values 
	('d82345', 45214, 'Pomegranete', 'Ms Peach', 'Peach Books', 1991);
insert into book values 
	('e12398', 95262, 'Passionfruit', 'Mr Apple', 'Apple Books', 1991);
insert into book values 
	('f11245', 78146, 'Dragonfruit', 'Ms Peach', 'Peach Books', 1991);
insert into book values 
	('h23423', 35153, 'Strawberry', 'Ms Banana', 'Peach Books', 1991);
insert into book values 
	('i12319', 54757, 'Pomegranete', 'Ms Peach', 'Peach Books', 1991);
insert into book values 
	('j01923', 78745, 'Passionfruit', 'Mr Apple', 'Apple Books', 1991);
insert into book values 
	('k08127', 79845, 'Dragonfruit', 'Ms Peach', 'Peach Books', 1391);
insert into book values 
	('l23422', 51387, 'Strawberry', 'Ms Banana', 'Peach Books', 2991);

insert into hasAuthor values
	('a12345', 'Ms Peach');
insert into hasAuthor values
	('a23456', 'Mr Apple');
insert into hasAuthor values
	('b34567', 'Ms Banana');
insert into hasAuthor values
	('c12235', 'Ms Peach');
insert into hasAuthor values
	('d82345', 'Ms Peach');
insert into hasAuthor values
	('e12398', 'Mr Apple');
insert into hasAuthor values
	('f11245', 'Ms Peach');
insert into hasAuthor values
	('h23423', 'Ms Banana');
insert into hasAuthor values
	('i12319', 'Ms Peach');
insert into hasAuthor values
	('j01923', 'Mr Apple');
insert into hasAuthor values 
	('k08127', 'Ms Peach');
insert into hasAuthor values
	('l23422', 'Ms Banana');

insert into hasSubject values
	('a12345', 'Fruit');
insert into hasSubject values
	('a23456', 'Fruit');
insert into hasSubject values
	('b34567', 'Fruit');
insert into hasSubject values
	('c12235', 'Fruit');
insert into hasSubject values
	('d82345', 'Vegetable');
insert into hasSubject values
	('e12398', 'Vegetable');
insert into hasSubject values
	('f11245', 'Vegetable');
insert into hasSubject values
	('h23423', 'Vegetable');
insert into hasSubject values
	('i12319', 'Vegetable');
insert into hasSubject values
	('j01923', 'Food');
insert into hasSubject values 
	('k08127', 'Food');
insert into hasSubject values
	('l23422', 'Food');

insert into BookCopy values
	('a12345', 1, 'in');
insert into BookCopy values
	('a12345', 2, 'in');
insert into BookCopy values
	('a12345', 3, 'in');
insert into BookCopy values
	('a23456', 1, 'in');
insert into BookCopy values
	('a23456', 2, 'in');
insert into BookCopy values
	('a23456', 3, 'in');
insert into BookCopy values
	('a23456', 4, 'in');
insert into BookCopy values
	('a23456', 5, 'in');
insert into BookCopy values
	('b34567', 1, 'in');
insert into BookCopy values
	('b34567', 2, 'in');
insert into BookCopy values
	('b34567', 3, 'in');
insert into BookCopy values
	('c12235', 1, 'in');
insert into BookCopy values
	('c12235', 2, 'in');
insert into BookCopy values
	('c12235', 3, 'in');
insert into BookCopy values
	('c12235', 4, 'in');
insert into BookCopy values
	('c12235', 5, 'in');
insert into BookCopy values
	('c12235', 6, 'in');
insert into BookCopy values
	('d82345', 1, 'in');
insert into BookCopy values
	('e12398', 1, 'in');
insert into BookCopy values
	('e12398', 2, 'in');
insert into BookCopy values
	('e12398', 3, 'in');
insert into BookCopy values
	('f11245', 1, 'in');
insert into BookCopy values
	('h23423', 1, 'in');
insert into BookCopy values
	('i12319', 1, 'in');
insert into BookCopy values
	('i12319', 2, 'in');
insert into BookCopy values
	('j01923', 1, 'in');
insert into BookCopy values 
	('k08127', 1, 'in');
insert into BookCopy values 
	('k08127', 2, 'in');
insert into BookCopy values
	('l23422', 1, 'in');
