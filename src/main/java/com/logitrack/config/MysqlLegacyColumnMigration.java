package com.logitrack.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Order(0)
public class MysqlLegacyColumnMigration implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(MysqlLegacyColumnMigration.class);

    private final JdbcTemplate jdbc;

    public MysqlLegacyColumnMigration(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            migrateVehicle();
            migrateMaintenance();
        } catch (Exception e) {
            log.warn("Migração opcional de colunas MySQL ignorada: {}", e.getMessage());
        }
    }

    private void migrateVehicle() {
        if (!tableExists("vehicle")) {
            return;
        }
        boolean hasType = columnExists("vehicle", "type");
        boolean hasVehicleType = columnExists("vehicle", "vehicle_type");
        if (hasType && hasVehicleType) {
            jdbc.execute("UPDATE vehicle SET vehicle_type = `type`");
            jdbc.execute("ALTER TABLE vehicle DROP COLUMN `type`");
            log.info("vehicle: coluna legada `type` removida (dados copiados para vehicle_type)");
        } else if (hasType && !hasVehicleType) {
            jdbc.execute("ALTER TABLE vehicle CHANGE COLUMN `type` vehicle_type VARCHAR(16) NOT NULL");
            log.info("vehicle: coluna `type` renomeada para vehicle_type");
        }
    }

    private void migrateMaintenance() {
        if (!tableExists("maintenance")) {
            return;
        }
        boolean hasType = columnExists("maintenance", "type");
        boolean hasKind = columnExists("maintenance", "maintenance_kind");
        if (hasType && hasKind) {
            jdbc.execute("UPDATE maintenance SET maintenance_kind = `type`");
            jdbc.execute("ALTER TABLE maintenance DROP COLUMN `type`");
            log.info("maintenance: coluna legada `type` removida (dados copiados para maintenance_kind)");
        } else if (hasType && !hasKind) {
            jdbc.execute("ALTER TABLE maintenance CHANGE COLUMN `type` maintenance_kind VARCHAR(64) NOT NULL");
            log.info("maintenance: coluna `type` renomeada para maintenance_kind");
        }
    }

    private boolean tableExists(String table) {
        Integer n = jdbc.queryForObject(
                "SELECT COUNT(*) FROM information_schema.TABLES WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ?",
                Integer.class,
                table);
        return n != null && n > 0;
    }

    private boolean columnExists(String table, String column) {
        Integer n = jdbc.queryForObject(
                """
                        SELECT COUNT(*) FROM information_schema.COLUMNS
                        WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ? AND COLUMN_NAME = ?
                        """,
                Integer.class,
                table,
                column);
        return n != null && n > 0;
    }
}
