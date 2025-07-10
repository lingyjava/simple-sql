-- name: CreateTable
CREATE TABLE IF NOT EXISTS ss_table_demo (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- name: SelectById
SELECT * FROM ss_table_demo WHERE id = ?;

-- name: SelectAll
SELECT * FROM ss_table_demo;

-- name: InsertTable
INSERT INTO ss_table_demo(name, description)
VALUES (?, ?);

-- name: UpdateTable
UPDATE ss_table_demo
SET name = ?, description = ?
WHERE id = ?;

-- name: DeleteById
DELETE FROM ss_table_demo WHERE id = ?;