-- Namen und PINs der Familie aktualisieren
UPDATE family_users SET display_name = 'Yvonne', pin_code = '1234' WHERE display_name = 'Mama'   AND role = 'PARENT';
UPDATE family_users SET display_name = 'Simon',  pin_code = '2345' WHERE display_name = 'Papa'   AND role = 'PARENT';
UPDATE family_users SET display_name = 'Tim'                        WHERE display_name = 'Kind 1' AND role = 'CHILD';
UPDATE family_users SET display_name = 'Chris'                      WHERE display_name = 'Kind 2' AND role = 'CHILD';
UPDATE family_users SET display_name = 'Jan'                        WHERE display_name = 'Kind 3' AND role = 'CHILD';
