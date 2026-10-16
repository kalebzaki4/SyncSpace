SET @patient_table_exists = (
    SELECT COUNT(*)
    FROM information_schema.tables
    WHERE table_schema = DATABASE()
      AND table_name = 'pacientes'
);

SET @patient_user_foreign_key = IF(
    @patient_table_exists = 0,
    NULL,
    (
        SELECT constraint_name
        FROM information_schema.key_column_usage
        WHERE table_schema = DATABASE()
          AND table_name = 'pacientes'
          AND column_name = 'usuario_id'
          AND referenced_table_name = 'usuarios'
        LIMIT 1
    )
);

SET @drop_patient_user_foreign_key = IF(
    @patient_user_foreign_key IS NULL,
    'SELECT 1',
    CONCAT('ALTER TABLE `pacientes` DROP FOREIGN KEY `', @patient_user_foreign_key, '`')
);

PREPARE drop_patient_user_foreign_key FROM @drop_patient_user_foreign_key;
EXECUTE drop_patient_user_foreign_key;
DEALLOCATE PREPARE drop_patient_user_foreign_key;

SET @reservation_patient_foreign_key = (
    SELECT constraint_name
    FROM information_schema.key_column_usage
    WHERE table_schema = DATABASE()
      AND table_name = 'reserva'
      AND column_name = 'paciente_id'
      AND referenced_table_name = 'pacientes'
    LIMIT 1
);

SET @reservation_patient_update_rule = (
    SELECT update_rule
    FROM information_schema.referential_constraints
    WHERE constraint_schema = DATABASE()
      AND table_name = 'reserva'
      AND constraint_name = @reservation_patient_foreign_key
    LIMIT 1
);

SET @reservation_patient_delete_rule = (
    SELECT delete_rule
    FROM information_schema.referential_constraints
    WHERE constraint_schema = DATABASE()
      AND table_name = 'reserva'
      AND constraint_name = @reservation_patient_foreign_key
    LIMIT 1
);

SET @drop_reservation_patient_foreign_key = IF(
    @reservation_patient_foreign_key IS NULL,
    'SELECT 1',
    CONCAT('ALTER TABLE `reserva` DROP FOREIGN KEY `', @reservation_patient_foreign_key, '`')
);

PREPARE drop_reservation_patient_foreign_key FROM @drop_reservation_patient_foreign_key;
EXECUTE drop_reservation_patient_foreign_key;
DEALLOCATE PREPARE drop_reservation_patient_foreign_key;

SET @make_patient_id_generated = IF(
    @patient_table_exists = 0,
    'SELECT 1',
    'ALTER TABLE `pacientes` MODIFY COLUMN `usuario_id` BIGINT NOT NULL AUTO_INCREMENT'
);

PREPARE make_patient_id_generated FROM @make_patient_id_generated;
EXECUTE make_patient_id_generated;
DEALLOCATE PREPARE make_patient_id_generated;

SET @restore_reservation_patient_foreign_key = IF(
    @reservation_patient_foreign_key IS NULL,
    'SELECT 1',
    CONCAT(
        'ALTER TABLE `reserva` ADD CONSTRAINT `', @reservation_patient_foreign_key,
        '` FOREIGN KEY (`paciente_id`) REFERENCES `pacientes` (`usuario_id`)',
        ' ON UPDATE ', @reservation_patient_update_rule,
        ' ON DELETE ', @reservation_patient_delete_rule
    )
);

PREPARE restore_reservation_patient_foreign_key FROM @restore_reservation_patient_foreign_key;
EXECUTE restore_reservation_patient_foreign_key;
DEALLOCATE PREPARE restore_reservation_patient_foreign_key;
