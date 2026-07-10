ALTER TABLE opening_stats DROP COLUMN opening_name;
ALTER TABLE opening_stats ADD COLUMN accuracy_sample_count INTEGER NOT NULL DEFAULT 0;