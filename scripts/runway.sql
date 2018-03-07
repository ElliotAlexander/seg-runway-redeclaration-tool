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
   ON UPDATE NO ACTION)
