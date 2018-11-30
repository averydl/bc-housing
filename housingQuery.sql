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
  appl_num       integer(9) primary key auto_increment,
  sid            integer(9) not null,
  appl_date      date,
  start_quarter  varchar(1) not null,
  end_quarter    varchar (1) not null,
  pref_sid       integer(9),
  status         varchar(1),
  foreign key (sid) references student(sid) on delete cascade,
  foreign key (pref_sid) references student(sid) on delete cascade
);

alter table application add constraint start_quarter check (start_quarter in ('F', 'W', 'S'));
alter table application add constraint end_quarter check (end_quarter in ('F', 'W', 'S'));
alter table application add constraint Status check (status in ('A', 'C', 'L', 'P'));
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


-- Adding 5 more people to the table so we have 6 male and 6 female students
INSERT INTO person VALUES(110000000,110000000,"Danerys","B","Targaryen","Dragonstone, Westeross 71101",'F',2065551100);
INSERT INTO person VALUES(120000000,120000000,"Arya","C","Stark","Braavos, Free Cities 91101",'F',2065551200);
INSERT INTO person VALUES(130000000,130000000,"Egrett","F","Wild","North of the Wall, Westeross 00000",'F',2065551300);


INSERT INTO person VALUES(140000000,140000000,"John","B","Snow","Winterfell, Westeross 81101",'M',2065551400);
INSERT INTO person VALUES(150000000,150000000,"Jaime","B","Lannister","Kings Landing, Westeross 66101",'M',2065551500);
INSERT INTO person VALUES(160000000,160000000,"Bron","B","Blackwater","Tarth, Westeross 99101",'M',2065551600);



INSERT INTO admin VALUES(444444444,"Housing Services","Database Administrator");
INSERT INTO admin VALUES(666666666,"Housing Services","Supervisor");


INSERT INTO student VALUES(111111111,"Chemistry","Arts and Sciences",true,'2020-06-15');
INSERT INTO student VALUES(222222222,"Political Science","Arts and Sciences",false,'1973-01-22');
INSERT INTO student VALUES(333333333,"Computer Science","Engineering",false,'2021-06-15');
INSERT INTO student VALUES(777777777,"Computer Science","Engineering",true,'1934-07-04');
INSERT INTO student VALUES(888888888,"Computer Science","Engineering",false,'1969-06-15');
INSERT INTO student VALUES(999999999,"Architecture","Arts and Sciences",false,'2020-06-15');
INSERT INTO student VALUES(100000000,"Aeronautics","Engineering",false,'1937-07-02');

-- Additional students

INSERT INTO student VALUES(110000000,"Dragons","Arts and Sciences",false,'2019-06-15');
INSERT INTO student VALUES(120000000,"Swords","Engineering",false,'2023-06-15');
INSERT INTO student VALUES(130000000,"Fletching","Engineering",false,'2022-06-15');
INSERT INTO student VALUES(140000000,"PeaceKeeper","Arts and Sciences",false,'2018-06-15');
INSERT INTO student VALUES(150000000,"General","Engineering",false,'2020-04-15');
INSERT INTO student VALUES(160000000,"Mooching","Engineering",true,'2011-03-15');
INSERT INTO student VALUES(555555555,"Computer Science", "Engineering", false,'2019-06-15');


INSERT INTO bed_type VALUES(1,"s",true,true,true,4667,"One Bedroom Suite, One Person");
INSERT INTO bed_type VALUES(2,"s",false,false,false,3667,"One Bedroom Suite, Two Persons");
INSERT INTO bed_type VALUES(3,"s",false,false,true,3500,"Two Bedroom Suite, Two Persons");
INSERT INTO bed_type VALUES(4,"s",false,true,false,2500,"Two Bedroom Suite, Three Persons - Shared Room");
INSERT INTO bed_type VALUES(5,"s",false,false,true,3500,"Two Bedroom Suite, Three Persons - Private Room");
INSERT INTO bed_type VALUES(6,"s",false,true,false,2500,"Two Bedroom Suite, Four Persons");
INSERT INTO bed_type VALUES(7,"a",false,false,false,3334,"Two Bedroom Apartment, Four Persons");
INSERT INTO bed_type VALUES(8,"a",false,false,true,4000,"Four Bedroom Apartment, Four Persons");


INSERT INTO bed VALUES(1,100,400,"3000 Landerholm Cir SE",'a',1);  -- 1b 1p aval
INSERT INTO bed VALUES(1,310,400,"3000 Landerholm Cir SE",'m',1);  -- 1b 1p m  need appl + lease SID 111111111

INSERT INTO bed VALUES(1,110,400,"3000 Landerholm Cir SE",'a',2);  -- 1b 2p aval
INSERT INTO bed VALUES(2,110,400,"3000 Landerholm Cir SE",'a',2);  -- 1b 2p aval  the same room
INSERT INTO bed VALUES(1,320,400,"3000 Landerholm Cir SE",'f',2);  -- 1b 2p 1f       need appl + lease SID 777777777
INSERT INTO bed VALUES(1,520,400,"3000 Landerholm Cir SE",'m',2);  -- 1b 2p 1m       need appl + lease SID 222222222

INSERT INTO bed VALUES(1,120,400,"3000 Landerholm Cir SE",'a',3);  -- 2b 2p aval
INSERT INTO bed VALUES(2,120,400,"3000 Landerholm Cir SE",'a',3);  -- 2b 2p aval   same room
INSERT INTO bed VALUES(1,400,400,"3000 Landerholm Cir SE",'m',3);  -- 2b 2p m SID 333333333
INSERT INTO bed VALUES(2,400,400,"3000 Landerholm Cir SE",'a',3);  -- 2b 2p aval   same room
INSERT INTO bed VALUES(1,500,400,"3000 Landerholm Cir SE",'f',3);  -- 2b 2p f SID 888888888
INSERT INTO bed VALUES(2,500,400,"3000 Landerholm Cir SE",'a',3);  -- 2b 2p aval   same room

INSERT INTO bed VALUES(1,130,400,"3000 Landerholm Cir SE",'a',4);  -- 2b 3p shared aval
INSERT INTO bed VALUES(2,130,400,"3000 Landerholm Cir SE",'a',4);  -- 2b 3p shared aval  same room
INSERT INTO bed VALUES(3,130,400,"3000 Landerholm Cir SE",'f',5);  -- 2b 3p priv f SID 999999999

INSERT INTO bed VALUES(1,410,400,"3000 Landerholm Cir SE",'a',4);  -- 2b 3p shared aval
INSERT INTO bed VALUES(2,410,400,"3000 Landerholm Cir SE",'m',4);  -- 2b 3p shared m     same room SID 120000000
INSERT INTO bed VALUES(3,410,400,"3000 Landerholm Cir SE",'a',5);  -- 2b 3p priv aval

INSERT INTO bed VALUES(1,510,400,"3000 Landerholm Cir SE",'f',4);  -- 2b 3p shared f SID 100000000
INSERT INTO bed VALUES(2,510,400,"3000 Landerholm Cir SE",'a',4);  -- 2b 3p shared aval     same room
INSERT INTO bed VALUES(3,510,400,"3000 Landerholm Cir SE",'a',5);  -- 2b 3p priv aval

INSERT INTO bed VALUES(1,220,400,"3000 Landerholm Cir SE",'a',6);  -- 2b 4p aval
INSERT INTO bed VALUES(2,220,400,"3000 Landerholm Cir SE",'a',6);  -- 2b 4p aval   same room
INSERT INTO bed VALUES(3,220,400,"3000 Landerholm Cir SE",'m',6);  -- 2b 4p m SID 140000000
INSERT INTO bed VALUES(4,220,400,"3000 Landerholm Cir SE",'a',6);  -- 2b 4p aval   same room

INSERT INTO bed VALUES(1,140,400,"3000 Landerholm Cir SE",'a',6);  -- 2b 4p aval
INSERT INTO bed VALUES(2,140,400,"3000 Landerholm Cir SE",'a',6);  -- 2b 4p aval   same room
INSERT INTO bed VALUES(3,140,400,"3000 Landerholm Cir SE",'f',6);  -- 2b 4p f SID 110000000
INSERT INTO bed VALUES(4,140,400,"3000 Landerholm Cir SE",'a',6);  -- 2b 4p aval   same room

INSERT INTO bed VALUES(1,230,400,"3000 Landerholm Cir SE",'f',7);  -- 2b 4p f SID 130000000
INSERT INTO bed VALUES(2,230,400,"3000 Landerholm Cir SE",'a',7);  -- 2b 4p aval   same room
INSERT INTO bed VALUES(3,230,400,"3000 Landerholm Cir SE",'a',7);  -- 2b 4p aval
INSERT INTO bed VALUES(4,230,400,"3000 Landerholm Cir SE",'a',7);  -- 2b 4p aval   same room


INSERT INTO bed VALUES(1,300,400,"3000 Landerholm Cir SE",'m',8);  -- 4b 4p m SID 150000000
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
INSERT INTO personVerification VALUES(110000000,"Dragons");
INSERT INTO personVerification VALUES(120000000,"Swords");
INSERT INTO personVerification VALUES(130000000,"Fletchin");
INSERT INTO personVerification VALUES(140000000,"PeaceK");
INSERT INTO personVerification VALUES(150000000,"General");
INSERT INTO personVerification VALUES(444444444,"Admin");
INSERT INTO personVerification VALUES(666666666,"Supervis");
INSERT INTO personVerification VALUES(160000000,"zzzzzzzz");
INSERT INTO personVerification VALUES(555555555,"Student");




INSERT INTO application VALUES(3, 111111111, '2018-09-04', 'F', 'S', 777777777, 'A'); -- m moved in
INSERT INTO application VALUES(1, 222222222, '2017-08-30', 'F', 'S', null, 'L'); -- m moved in         past
INSERT INTO application VALUES(4, 333333333, '2018-09-10', 'F', 'W', null, 'L'); -- m moved in
INSERT INTO application VALUES(5, 777777777, '2018-09-13', 'F', 'S', 111111111, 'A'); -- f moved in
INSERT INTO application VALUES(6, 888888888, '2018-10-13', 'W', 'S', null, 'L'); -- f moved in
INSERT INTO application VALUES(7, 999999999, '2018-11-01', 'W', 'W', null, 'L'); -- f moved in
INSERT INTO application VALUES(8, 100000000, '2018-11-10', 'W', 'W', null, 'L'); -- f moved in
INSERT INTO application VALUES(9, 110000000, '2018-11-10', 'W', 'W', null, 'L'); -- f moved in
INSERT INTO application VALUES(10, 120000000, '2018-11-01', 'W', 'S', null, 'A'); -- m moved in
INSERT INTO application VALUES(11, 130000000, '2018-11-20', 'S', 'S', null, 'L'); -- f moved in
INSERT INTO application VALUES(13, 140000000, '2019-11-20', 'W', 'S', null, 'L'); -- m moved in
INSERT INTO application VALUES(14, 150000000, '2019-11-20', 'S', 'S', null, 'L'); -- m moved in
INSERT INTO application VALUES(12, 222222222, '2018-11-20', 'W', 'S', null, 'L'); -- m moved in
INSERT INTO application VALUES(2, 555555555, '2018-08-30', 'F', 'S', null, 'L'); -- m moved in



INSERT INTO application_preference VALUES(3, 1);
INSERT INTO application_preference VALUES(3, 4);
INSERT INTO application_preference VALUES(3, 6);
INSERT INTO application_preference VALUES(5, 1);
INSERT INTO application_preference VALUES(5, 4);
INSERT INTO application_preference VALUES(5, 6);
INSERT INTO application_preference VALUES(10, 3);
INSERT INTO application_preference VALUES(10, 5);
INSERT INTO application_preference VALUES(10, 8);


INSERT INTO lease VALUES(1, 1, '2017-09-03', '2018-06-15', 14001.00, 1, 310);
INSERT INTO lease VALUES(2, 2, '2018-09-03', '2019-06-15', 11001.00, 1, 520);
INSERT INTO lease VALUES(3, 4, '2018-09-03', '2019-03-15', 9334.00, 1, 310);
INSERT INTO lease VALUES(4, 6, '2019-01-03', '2019-06-15', 7334.00, 1, 320);
INSERT INTO lease VALUES(5, 7, '2019-01-03', '2019-03-15', 3500.00, 1, 500);
INSERT INTO lease VALUES(6, 12, '2019-01-03', '2019-06-15', 5000.00, 2, 410);
INSERT INTO lease VALUES(7, 8, '2019-01-03', '2019-03-15', 3500.00, 3, 130);
INSERT INTO lease VALUES(8, 9, '2019-01-03', '2019-03-15', 2500.00, 1, 510);
INSERT INTO lease VALUES(9, 11, '2019-04-03', '2019-06-15', 3334.00, 1, 230);
INSERT INTO lease VALUES(10, 13, '2019-01-03', '2019-06-15', 8000.00, 1, 300);
INSERT INTO lease VALUES(11, 14, '2019-04-03', '2019-06-15', 2500.00, 3, 220);


-- need to check
INSERT INTO maintenanceRequest VALUES(1, 2, 'Grace Hopper', 'Kitchen lights burnt out', '2018-10-03', null);
INSERT INTO maintenanceRequest VALUES(2, 4, 'Grace Hopper', 'Sink clogged', '2018-11-04', '2018-11-05');
INSERT INTO maintenanceRequest VALUES(3, 2, 'Grace Hopper', 'Sink clogged', '2018-11-03', null);
INSERT INTO maintenanceRequest VALUES(4, 1, 'Grace Hopper', 'Microwave no longer works', '2017-11-29', '2018-12-04');


DELIMITER //

-- DROP PROCEDURE IF EXISTS getActiveStudentApplication;

CREATE PROCEDURE getActiveStudentApplication (IN studentId integer(9))
BEGIN
    SELECT appl_num, appl_date, start_quarter, end_quarter, pref_sid
    FROM application
    WHERE sid = studentId AND
          status = 'A'
	LIMIT 1;
END

DELIMITER ;


DELIMITER //

-- DROP PROCEDURE IF EXISTS getApplicationPreferences;

CREATE PROCEDURE getApplicationPreferences (IN applNum integer(9))
BEGIN
    SELECT bd_id
    FROM application_preference
    WHERE appl_num = applNum
	LIMIT 3;
END

DELIMITER ;

DELIMITER //

-- DROP PROCEDURE IF EXISTS getActiveStudentLease;

CREATE PROCEDURE getActiveStudentLease (IN studentId integer(9))
BEGIN
    SELECT lid, lease.appl_num, lease.start_date, lease.end_date, cost, bed_num, unit_num
    FROM lease, application
    WHERE application.sid = studentId AND
		  lease.appl_num = application.appl_num AND
          lease.end_date > CURDATE()
	LIMIT 1;
END

DELIMITER ;

-- CALL getActiveStudentLease (555555555);  -- for check

DELIMITER //

--  DROP PROCEDURE IF EXISTS getActiveRequests;

CREATE PROCEDURE getActiveRequests (IN studentId integer(9))
BEGIN
    SELECT rid, description, date_sent
    FROM maintenanceRequest, lease, application
    WHERE application.sid = studentId AND
		  lease.appl_num = application.appl_num AND
          lease.lid = maintenanceRequest.lid AND
          date_resolv IS NULL;
END

DELIMITER ;

DELIMITER //

--  DROP PROCEDURE IF EXISTS updateCurrentApplication;

CREATE PROCEDURE updateCurrentApplication (IN applNum integer(9),
				 startQ varchar(1), endQ varchar(1), prefSid integer(9), INOUT upload bool)
BEGIN
    -- Update
    UPDATE application
    SET appl_date = CURDATE(),  start_quarter = startQ, end_quarter = endQ, pref_sid = prefSid
    WHERE appl_num = applNum;
    -- return
    set upload = true;
END

DELIMITER ;


DELIMITER //

--  DROP PROCEDURE IF EXISTS updateCurrentApplicationPrefer;

CREATE PROCEDURE updateCurrentApplicationPrefer (IN applNum integer(9), firstPr integer(2),
				 secondPr integer(2), thirdPr integer(2), INOUT upload bool)
BEGIN
    -- delete
    DELETE FROM application_preference
    WHERE appl_num = applNum;

    -- insert
    INSERT INTO application_preference VALUES (applNum, firstPr);
    IF (firstPr != secondPr)
    THEN INSERT INTO application_preference VALUES (applNum, secondPr);
    END IF;
    IF (firstPr != thirdPr AND secondPr != thirdPr)
    THEN INSERT INTO application_preference VALUES (applNum, thirdPr);
    END IF;

    -- return
    set upload = true;
END

DELIMITER ;

DELIMITER //

--  DROP PROCEDURE IF EXISTS cancelCurrentApplication;

CREATE PROCEDURE cancelCurrentApplication(IN applNum integer(9), INOUT upload bool)
BEGIN
    -- Update
    UPDATE application
    SET status = 'C'
    WHERE appl_num = applNum;
    -- return
    set upload = true;
END

DELIMITER ;



DELIMITER //

-- DROP PROCEDURE IF EXISTS createNewApplication;

CREATE PROCEDURE createNewApplication(IN sId integer(9),
				 startQ varchar(1), endQ varchar(1), prefSid integer(9), OUT num integer)
BEGIN
    -- insert
    INSERT INTO application (sid, appl_date, start_quarter, end_quarter, pref_sid, status)
    VALUES (sId, CURDATE(), startQ, endQ, prefSid, 'A');

    -- return
    SELECT appl_num INTO num
    FROM application
    WHERE appl_num = LAST_INSERT_ID();
END

DELIMITER ;

CREATE PROCEDURE getVacantUnits(IN quarterDate)
BEGIN
  SELECT unit_num, bed_num, end_date
  FROM lease
  WHERE end_date >= quarterDate;
END




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
