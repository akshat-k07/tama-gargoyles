-- ==================================
-- Seed Data for users (7 records)
-- ==================================

INSERT INTO users (username, email, rocks, bugs, mystery_food)
VALUES
  ('stoneking',   'stoneking@example.com',   100,  50,  5),
  ('nightwatch',  'nightwatch@example.com',   80, 120,  2),
  ('rookie',      'rookie@example.com',       20,  10,  0),
  ('skywarden',   'skywarden@example.com',    60,  40,  3),
  ('cryptlord',   'cryptlord@example.com',   150, 200, 10),
  ('sunseeker',   'sunseeker@example.com',    45,  25,  1),
  ('towerguard',  'towerguard@example.com',   90,  70,  4);

-- ==================================
-- Seed Data for gargoyles (7 records)
-- All FLOAT times are 24-hour clock
-- ==================================

INSERT INTO gargoyles (name,age,type,status,hunger,happiness,health,experience,strength,speed,intelligence,last_fed,last_played,left_at,user_id)
VALUES
  ('Grimclaw',120,'BAD','ACTIVE',70,40,90,300,25,15,10,6.50,19.25,21.75,1),
  ('Sunspire',80,'GOOD','ACTIVE',30,85,100,500,18,20,22,8.00,13.75,14.50,1),
  ('Pebble',5,'CHILD','ACTIVE',60,75,80,40,6,8,12,7.25,10.50,9.25,2),
  ('Ashwing',200,'BAD','RETIRED',10,50,60,1200,30,12,18,2.00,3.50,3.00,5),
  ('Moonbrick',45,'GOOD','ACTIVE',40,90,95,260,14,18,25,12.00,17.25,18.50,4),
  ('Craglet',12,'CHILD','ACTIVE',55,65,85,90,9,11,14,6.75,8.25,7.75,6),
  ('Ironmaw',160,'BAD','ACTIVE',80,30,88,800,28,10,16,22.00,23.25,23.25,7);
