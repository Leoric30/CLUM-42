-- =============================================================
-- V4__add_roles.sql
-- Agrega los roles de directiva faltantes al catálogo de roles
-- del club. Estos roles son necesarios para el sistema de
-- dashboards diferenciados por función.
-- =============================================================

INSERT INTO roles (nombre, descripcion) VALUES
    ('SUBDIRECTOR', 'Apoyo directo al Director. Acceso a revisiones y propuestas.'),
    ('LOGISTICA',   'Encargado de planes de trabajo, tareas y organización de eventos.'),
    ('MEDIA',       'Responsable del contenido multimedia y comunicación visual del club.'),
    ('CAPELLAN',    'Encargado del ámbito espiritual: reuniones, estudios bíblicos y actividades de integración.')
ON CONFLICT (nombre) DO NOTHING;
