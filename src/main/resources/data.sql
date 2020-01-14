INSERT INTO aria_user VALUES (0, 'root')
ON CONFLICT DO NOTHING;
INSERT INTO aria_group VALUES (0, 'root', 0, true, true)
ON CONFLICT DO NOTHING;
