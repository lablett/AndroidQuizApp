-- drop table public.questions;

create table public.users (
							uid serial not null primary key,
							username character varying (30) not null,
							email character varying (50) not null,
							score integer,
							
							constraint username_unique unique (username),
							constraint uid_unique unique (uid)							

								);

create table public.questions (
								qid serial not null primary key,
								point_name character varying(50) not null,
								question character varying(200) not null,
								answer1 character varying (30) not null,
								answer2 character varying (30) not null,
								answer3 character varying (30) not null,
								answer4 character varying (30) not null,
								answer_correct integer not null,
								coordinates geometry not null,
							
							constraint questions_unique unique (question, coordinates),								
							constraint point_unique unique (coordinates),
							constraint id_unique unique (point_name),
							
							constraint answer_correct_check check (answer_correct > 0 and answer_correct <= 4)
								);

insert into public.questions (point_name, question, answer1, answer2, answer3, answer4, answer_correct, coordinates) values ('Euston Square', 'What year did Euston Square station open?', '1863', '1909', '1925', '1963', '1', st_geomfromtext('POINT(-0.13553202152252197 51.52579379667926)'));
insert into public.questions (point_name, question, answer1, answer2, answer3, answer4, answer_correct, coordinates) values ('UCL Main Entrance', 'How many students are enrolled at UCL?', '25,658', '39,473', '52,135', '37,253', '2', st_geomfromtext('POINT(-0.13458788394927979 51.52426851607867)'));
insert into public.questions (point_name, question, answer1, answer2, answer3, answer4, answer_correct, coordinates) values ('Cruciform', 'What year was the crufiform building built?', '1919', '1889', '1901', '1906', '4', st_geomfromtext('POINT(-0.13479173183441162 51.52442872300703)'));
insert into public.questions (point_name, question, answer1, answer2, answer3, answer4, answer_correct, coordinates) values ('Gordon Square Gardens', 'Who is Gordon Square Gardens named after?', 'Lady Georgine Gordon', 'Lord George Gordon', 'Lady Georgiana Gordon', 'Lord Geoffrey Gordon', '3', st_geomfromtext('POINT(-0.13070940971374512 51.52419508771479)'));
insert into public.questions (point_name, question, answer1, answer2, answer3, answer4, answer_correct, coordinates) values ('Euston Church', 'What grade of listed building is Euston Church?', 'I', 'II', 'II*', 'III', '1', st_geomfromtext('POINT(-0.13091862201690674 51.523380692467136)'));
insert into public.questions (point_name, question, answer1, answer2, answer3, answer4, answer_correct, coordinates) values ('Students Union', 'What is the postcode of Student Central?', 'WC1E 7GZ', 'WC1E 7DW', 'WC1E 7AB', 'WC1E 7HY', '4', st_geomfromtext('POINT(-0.13151943683624268 51.52261968702276)'));


create table public.answers (
							aid serial not null primary key,
							user_id integer not null,
							question_id integer not null,
							question character varying (100) not null,
							answer character varying (30) not null,
							correct_boolean boolean not null,
							imei character varying(20) not null,
							
							foreign key (user_id) references public.users (uid),
							foreign key (question_id) references public.questions (qid)
							);