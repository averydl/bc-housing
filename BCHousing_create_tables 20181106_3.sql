CREATE TABLE person (
  bc_id    integer(9) primary key,
  ssn      char(9) unique not null,
  fname    varchar(15) not null, 
  minit    varchar(1),
  lname    varchar(15) not null,
  address  varchar(50),
  sex      char not null,
  phone    integer(10)
);

alter table person add constraint sex check (sex in ('F', 'M'));

CREATE TABLE admin (
  staff_id    integer(9) primary key,
  department  varchar(25),
  job_title   varchar(25), 
  foreign key (staff_id) references person(bc_id) on delete cascade
);

CREATE TABLE personVerification (
  bc_id    integer(9),
  password  varchar(8),
  primary key (bc_id, password),
  foreign key (bc_id) references person(bc_id) on delete cascade
);

CREATE TABLE student (
  sid      integer(9) primary key,
  department varchar(50), 
  college    varchar(50),
  isMarried  bool,
  grad_date  date,
  foreign key (sid) references person(bc_id) on delete cascade
);


CREATE TABLE bed_type (
  bd_id       integer(2) primary key auto_increment,
  unit_type   varchar(1) not null,
  isOneBd     bool not null,
  isMar_ok    bool not null,
  isPrivate   bool not null,
  price       decimal(10,2),
  description varchar(50)
);

alter table bed_type add constraint unit_type check (unit_type in ('A', 'S'));

CREATE TABLE application(
  appl_num  integer(9) primary key auto_increment,
  sid       integer(9) not null,
  appl_date date,
  start_quarter varchar(1) not null,
  end_quarter varchar (1) not null,
  pref_sid  integer(9),
  foreign key (sid) references student(sid) on delete cascade,
  foreign key (pref_sid) references student(sid) on delete cascade
);

alter table application add constraint start_quarter check (start_quarter in ('F', 'W', 'S'));
alter table application add constraint end_quarter check (end_quarter in ('F', 'W', 'S'));
-- alter table application add constraint pref_sid check (pref_sid != sid);    logic to java

CREATE TABLE application_preference(
  appl_num  integer(9),
  bd_id     integer(2),
  primary key(appl_num, bd_id),
  foreign key(appl_num) references application(appl_num) on delete cascade,
  foreign key(bd_id) references bed_type(bd_id) on delete cascade
);

CREATE TABLE bed (
  bed_num          varchar(1),
  unit_num         integer(3),
  building_num     integer(6) not null,
  street_address   varchar(25),
  available_status varchar(1) not null,
  bd_id            integer(2),
  primary key (bed_num, unit_num),
  foreign key (bd_id) references bed_type(bd_id) on delete cascade
);

alter table bed add constraint available_status check (available_status in ('A', 'F', 'M', 'R'));

CREATE TABLE lease (
  lid         integer(9) primary key auto_increment,
  appl_num    integer(9) not null,
  start_date  date not null,
  end_date    date not null,
  cost        decimal(10,2),
  bed_num     varchar(1) not null,
  unit_num    integer(3) not null,
  foreign key (appl_num) references application(appl_num) on delete cascade,
  foreign key (bed_num, unit_num) references bed(bed_num, unit_num) on delete cascade
);

CREATE TABLE maintenanceRequest(
  rid          integer(9) primary key auto_increment,
  lid          integer(9),
  empl_name    varchar(15),
  description  varchar(50),
  date_sent    date,
  date_resolv  date,
  foreign key (lid) references lease(lid)
);

INSERT INTO person VALUES(111111111,111111111,"Walter","H","White","308 Negra Arroyo Ln, Albuquerque, NM 87111 USA",'M',2065551111);
INSERT INTO person VALUES(222222222,222222222,"Lyndon","B","Johnson","123 Dirt Path, Stonewall, TX 98125 USA",'M',2065552222);
INSERT INTO person VALUES(333333333,333333333,"Jeffrey","P","Bezos","999 Bezos' Big Balls, Seattle, WA 98125 USA",'M',2065553333);
INSERT INTO person VALUES(444444444,444444444,"William","H","Gates","400 Lk Wash Blvd, Seattle, WA 98101 USA",'M',2065554444);
INSERT INTO person VALUES(555555555,555555555,"Alan","M","Turing","505 Maida Vale Rd, Maida Vale, UK",'M',2065555555);
INSERT INTO person VALUES(666666666,666666666,"Grace","B","Hopper","255 Main St, New York City, NY 10001",'F',2065556666);
INSERT INTO person VALUES(777777777,777777777,"Marie","S","Curie","404 Road Not Found, Warsaw, PL",'F',2065557777);
INSERT INTO person VALUES(888888888,888888888,"Barbara","J","Liskov","200 OK Road, Los Angeles, CA 90895",'F',2065558888);
INSERT INTO person VALUES(999999999,999999999,"Jane","A","Doe","123 Alaskan Way, Seattle, WA 98125",'F',2065559999);
INSERT INTO person VALUES(100000000,100000000,"Amelia","M","Earhart","500 Grass Field Rd, Atchison, KS 66101",'F',2065550000);


INSERT INTO admin VALUES(444444444,"Housing Services","Database Administrator");
INSERT INTO admin VALUES(666666666,"Housing Services","Supervisor");


INSERT INTO student VALUES(111111111,"Chemistry","Arts and Sciences",true,'2020-06-15');
INSERT INTO student VALUES(222222222,"Political Science","Arts and Sciences",false,'1973-01-22');
INSERT INTO student VALUES(333333333,"Computer Science","Engineering",false,'2021-06-15');
INSERT INTO student VALUES(777777777,"Computer Science","Engineering",true,'1934-07-04');
INSERT INTO student VALUES(888888888,"Computer Science","Engineering",false,'1969-06-15');
INSERT INTO student VALUES(999999999,"Architecture","Arts and Sciences",false,'2020-06-15');
INSERT INTO student VALUES(100000000,"Aeronautics","Engineering",false,'1937-07-02');


INSERT INTO bed_type VALUES(1,"s",true,true,true,4667,"One Bedroom Suite, One Person");
INSERT INTO bed_type VALUES(2,"s",false,false,false,3667,"One Bedroom Suite, Two Persons");
INSERT INTO bed_type VALUES(3,"s",false,false,true,3500,"Two Bedroom Suite, Two Persons");
INSERT INTO bed_type VALUES(4,"s",false,true,false,2500,"Two Bedroom Suite, Three Persons - Shared Room");
INSERT INTO bed_type VALUES(5,"s",false,false,true,3500,"Two Bedroom Suite, Three Persons - Private Room");
INSERT INTO bed_type VALUES(6,"s",false,true,false,2500,"Two Bedroom Suite, Four Persons");
INSERT INTO bed_type VALUES(7,"a",false,false,false,3334,"Two Bedroom Apartment, Four Persons");
INSERT INTO bed_type VALUES(8,"a",false,false,true,4000,"Four Bedroom Apartment, Four Persons");


INSERT INTO bed VALUES(1,100,400,"3000 Landerholm Cir SE",'a',1);  -- 1b 1p aval
INSERT INTO bed VALUES(1,310,400,"3000 Landerholm Cir SE",'m',1);  -- 1b 1p m  need appl + lease

INSERT INTO bed VALUES(1,110,400,"3000 Landerholm Cir SE",'a',2);  -- 1b 2p aval
INSERT INTO bed VALUES(2,110,400,"3000 Landerholm Cir SE",'a',2);  -- 1b 2p aval  the same room
INSERT INTO bed VALUES(1,320,400,"3000 Landerholm Cir SE",'f',2);  -- 1b 2p 1f       need appl + lease
INSERT INTO bed VALUES(1,520,400,"3000 Landerholm Cir SE",'m',2);  -- 1b 2p 1m       need appl + lease

INSERT INTO bed VALUES(1,120,400,"3000 Landerholm Cir SE",'a',3);  -- 2b 2p aval
INSERT INTO bed VALUES(2,120,400,"3000 Landerholm Cir SE",'a',3);  -- 2b 2p aval   same room
INSERT INTO bed VALUES(1,400,400,"3000 Landerholm Cir SE",'m',3);  -- 2b 2p m
INSERT INTO bed VALUES(2,400,400,"3000 Landerholm Cir SE",'a',3);  -- 2b 2p aval   same room
INSERT INTO bed VALUES(1,500,400,"3000 Landerholm Cir SE",'f',3);  -- 2b 2p f
INSERT INTO bed VALUES(2,500,400,"3000 Landerholm Cir SE",'a',3);  -- 2b 2p aval   same room

INSERT INTO bed VALUES(1,130,400,"3000 Landerholm Cir SE",'a',4);  -- 2b 3p shared aval
INSERT INTO bed VALUES(2,130,400,"3000 Landerholm Cir SE",'a',4);  -- 2b 3p shared aval  same room
INSERT INTO bed VALUES(3,130,400,"3000 Landerholm Cir SE",'f',5);  -- 2b 3p priv f  

INSERT INTO bed VALUES(1,410,400,"3000 Landerholm Cir SE",'a',4);  -- 2b 3p shared aval
INSERT INTO bed VALUES(2,410,400,"3000 Landerholm Cir SE",'m',4);  -- 2b 3p shared m     same room
INSERT INTO bed VALUES(3,410,400,"3000 Landerholm Cir SE",'a',5);  -- 2b 3p priv aval

INSERT INTO bed VALUES(1,510,400,"3000 Landerholm Cir SE",'f',4);  -- 2b 3p shared f
INSERT INTO bed VALUES(2,510,400,"3000 Landerholm Cir SE",'a',4);  -- 2b 3p shared aval     same room
INSERT INTO bed VALUES(3,510,400,"3000 Landerholm Cir SE",'a',5);  -- 2b 3p priv aval

INSERT INTO bed VALUES(1,220,400,"3000 Landerholm Cir SE",'a',6);  -- 2b 4p aval
INSERT INTO bed VALUES(2,220,400,"3000 Landerholm Cir SE",'a',6);  -- 2b 4p aval   same room
INSERT INTO bed VALUES(3,220,400,"3000 Landerholm Cir SE",'m',6);  -- 2b 4p m
INSERT INTO bed VALUES(4,220,400,"3000 Landerholm Cir SE",'a',6);  -- 2b 4p aval   same room

INSERT INTO bed VALUES(1,140,400,"3000 Landerholm Cir SE",'a',6);  -- 2b 4p aval
INSERT INTO bed VALUES(2,140,400,"3000 Landerholm Cir SE",'a',6);  -- 2b 4p aval   same room
INSERT INTO bed VALUES(3,140,400,"3000 Landerholm Cir SE",'f',6);  -- 2b 4p f
INSERT INTO bed VALUES(4,140,400,"3000 Landerholm Cir SE",'a',6);  -- 2b 4p aval   same room

INSERT INTO bed VALUES(1,230,400,"3000 Landerholm Cir SE",'f',7);  -- 2b 4p f
INSERT INTO bed VALUES(2,230,400,"3000 Landerholm Cir SE",'a',7);  -- 2b 4p aval   same room
INSERT INTO bed VALUES(3,230,400,"3000 Landerholm Cir SE",'a',7);  -- 2b 4p aval   
INSERT INTO bed VALUES(4,230,400,"3000 Landerholm Cir SE",'a',7);  -- 2b 4p aval   same room


INSERT INTO bed VALUES(1,300,400,"3000 Landerholm Cir SE",'m',8);  -- 4b 4p m
INSERT INTO bed VALUES(2,300,400,"3000 Landerholm Cir SE",'a',8);  -- 4b 4p aval
INSERT INTO bed VALUES(3,300,400,"3000 Landerholm Cir SE",'a',8);  -- 4b 4p aval
INSERT INTO bed VALUES(4,300,400,"3000 Landerholm Cir SE",'a',8);  -- 4b 4p aval


INSERT INTO personVerification VALUES(111111111,"Chem");
INSERT INTO personVerification VALUES(222222222,"Science");
INSERT INTO personVerification VALUES(333333333,"Computer");
INSERT INTO personVerification VALUES(777777777,"Engin");
INSERT INTO personVerification VALUES(888888888,"CompSE");
INSERT INTO personVerification VALUES(999999999,"Architec");
INSERT INTO personVerification VALUES(100000000,"Aeronaut");
INSERT INTO personVerification VALUES(444444444,"Admin");
INSERT INTO personVerification VALUES(666666666,"Supervis");

INSERT INTO application VALUES();

DROP TABLE maintenanceRequest;
DROP TABLE lease;
DROP TABLE bed;
DROP TABLE application_preference;
DROP TABLE application;
DROP TABLE bed_type;
DROP TABLE personVerification;
DROP TABLE student;
DROP TABLE admin;
DROP TABLE person;