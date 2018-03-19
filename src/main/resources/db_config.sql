CREATE TABLE IF NOT EXISTS `runway` (
 `runway_id` INT NOT NULL,
 `airport_id` VARCHAR NULL,
 `physical_runway_id` INT NOT NULL,
 `runway_designator` VARCHAR(45) NULL,
 `tora` INT NULL,
 `toda` INT NULL,
 `asda` INT NULL,
 `lda` INT NULL,
 `remarks` VARCHAR(45) NULL,
 PRIMARY KEY (`runway_id`),
 CONSTRAINT `airport_id`
   FOREIGN KEY (`runway_id`)
   REFERENCES `airport` (`airport_id`)
   ON DELETE NO ACTION
   ON UPDATE NO ACTION);

CREATE TABLE IF NOT EXISTS `obstacle`(
`obstacle_id` INT NOT NULL,
`obstacle_name` VARCHAR(45) NULL,
`length` INT NULL,
`height` INT NULL,
PRIMARY KEY(`obstacle_id`));


CREATE TABLE IF NOT EXISTS `airport` (
 `airport_id` VARCHAR(4) NOT NULL,
 `airport_name` VARCHAR(45) NULL,
 `no_runways` INT NULL,
 PRIMARY KEY (`airport_id`));


