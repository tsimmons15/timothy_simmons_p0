create table if not exists Client (
	client_id serial primary key,
	client_name varchar(50) not null,
	client_username varchar(30) not null unique,
	client_password varchar(32) not null,
	client_salt varchar(32) not null
); 
select * from client;
select * from account;
select * from account_owner;

delete from account_owner;
delete from client;
delete from account;
alter table client add column client_password varchar(32) not null;
alter table client add column client_salt char(24) not null;

create table if not exists Account_Owner (
	client_id int,
	account_id int
);
alter table Account_Owner add primary key (client_id, account_id);
alter table Account_Owner add constraint ao_client_fk foreign key(client_id) references Client(client_id);
alter table Account_Owner add constraint ao_account_fk foreign key(account_id) references Account(account_id);
select * from account_owner;

create table if not exists Account (
	account_id serial primary key,
	account_balance float(32) default 0,
	account_type varchar(10) not null default 'Checking'
);
select * from account;


insert into client (client_name, client_username, client_password, client_salt) values ('Testing', 'testing', 'pass', 'passsalt');
insert into client (client_name, client_username, client_password, client_salt) values ('Testing', 'testing1', 'pass', 'passsalt');
insert into client (client_name, client_username, client_password, client_salt) values ('Testing', 'testing2', 'pass', 'passsalt');
insert into client (client_name, client_username, client_password, client_salt) values ('Testing', 'testing3', 'pass', 'passsalt');

insert into account (account_balance) values (0);

select * from account;
select * from client;
select * from account_owner;

insert into account_owner (client_id, account_id) values (175, 119);
insert into account_owner (client_id, account_id) values (176, 120);
insert into account_owner (client_id, account_id) values (177, 120);


select * from account a inner join account_owner ao on a.account_id = ao.account_id 
  where ao.client_id = 116 and a.account_id in ( null );