SET @medicos_table_exists = (
    SELECT COUNT(*)
    FROM information_schema.tables
    WHERE table_schema = DATABASE()
      AND table_name = 'medicos'
);

SET @medico_specialty_column_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'medicos'
      AND column_name = 'especialidade'
);

SET @medico_specialties_table_exists = (
    SELECT COUNT(*)
    FROM information_schema.tables
    WHERE table_schema = DATABASE()
      AND table_name = 'medico_especialidades'
);

SET @create_medico_specialties_table = IF(
    @medicos_table_exists = 1 AND @medico_specialties_table_exists = 0,
    'CREATE TABLE `medico_especialidades` (`medico_id` BIGINT NOT NULL, `especialidade` VARCHAR(255) NOT NULL, PRIMARY KEY (`medico_id`, `especialidade`), CONSTRAINT `fk_medico_especialidades_medico` FOREIGN KEY (`medico_id`) REFERENCES `medicos` (`usuario_id`) ON DELETE CASCADE)',
    'SELECT 1'
);

PREPARE create_medico_specialties_table FROM @create_medico_specialties_table;
EXECUTE create_medico_specialties_table;
DEALLOCATE PREPARE create_medico_specialties_table;

SET @copy_medico_specialty = IF(
    @medicos_table_exists = 1
        AND @medico_specialty_column_exists = 1
        AND @medicos_table_exists = 1,
    'INSERT IGNORE INTO `medico_especialidades` (`medico_id`, `especialidade`) SELECT `usuario_id`, `especialidade` FROM `medicos` WHERE `especialidade` IS NOT NULL',
    'SELECT 1'
);

PREPARE copy_medico_specialty FROM @copy_medico_specialty;
EXECUTE copy_medico_specialty;
DEALLOCATE PREPARE copy_medico_specialty;

SET @drop_medico_specialty_column = IF(
    @medicos_table_exists = 1 AND @medico_specialty_column_exists = 1,
    'ALTER TABLE `medicos` DROP COLUMN `especialidade`',
    'SELECT 1'
);

PREPARE drop_medico_specialty_column FROM @drop_medico_specialty_column;
EXECUTE drop_medico_specialty_column;
DEALLOCATE PREPARE drop_medico_specialty_column;
