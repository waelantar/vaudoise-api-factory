-- Insert a sample person client
INSERT INTO clients (name, email, phone, client_type, birthdate)
VALUES (
           'Alice Smith',
           'alice.smith@example.com',
           '+41791112233',
           'PERSON',
           '1985-05-20'
       );

-- Insert a sample company client
INSERT INTO clients (name, email, phone, client_type, company_identifier)
VALUES (
           'Globex Corporation',
           'contact@globex.com',
           '+4122334455',
           'COMPANY',
           'CHE-123.456.789'
       );

-- Insert an active contract for Alice Smith
INSERT INTO contracts (client_id, start_date, cost_amount, cost_currency)
VALUES (
           (SELECT id FROM clients WHERE email = 'alice.smith@example.com'),
           CURRENT_DATE - INTERVAL '1 year',
           150.75,
           'CHF'
       );

-- Insert an inactive contract for Globex Corporation
INSERT INTO contracts (client_id, start_date, end_date, cost_amount, cost_currency)
VALUES (
           (SELECT id FROM clients WHERE email = 'contact@globex.com'),
           CURRENT_DATE - INTERVAL '2 years',
           CURRENT_DATE - INTERVAL '6 months',
           500.00,
           'CHF'
       );