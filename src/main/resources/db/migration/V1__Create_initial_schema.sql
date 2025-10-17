-- Create the clients table using single-table inheritance strategy
CREATE TABLE clients (
                         id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                         name VARCHAR(255) NOT NULL,
                         email VARCHAR(255) NOT NULL UNIQUE,
                         phone VARCHAR(50) NOT NULL,
                         client_type VARCHAR(31) NOT NULL, -- Discriminator column for PERSON/COMPANY
                         created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Add columns specific to each client type
ALTER TABLE clients
    ADD COLUMN birthdate DATE, -- For PERSON
    ADD COLUMN company_identifier VARCHAR(255) UNIQUE; -- For COMPANY

-- Create the contracts table
CREATE TABLE contracts (
                           id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                           client_id UUID NOT NULL,
                           start_date DATE NOT NULL,
                           end_date DATE, -- NULL means the contract is still active
                           cost_amount DECIMAL(19, 2) NOT NULL,
                           cost_currency VARCHAR(3) NOT NULL DEFAULT 'CHF',
                           created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           CONSTRAINT fk_contract_client
                               FOREIGN KEY(client_id)
                                   REFERENCES clients(id)
                                   ON DELETE CASCADE
);

-- Create indexes for performance
CREATE INDEX idx_clients_email ON clients(email);
CREATE INDEX idx_clients_company_identifier ON clients(company_identifier);
CREATE INDEX idx_contracts_client_id ON contracts(client_id);
CREATE INDEX idx_contracts_dates ON contracts(start_date, end_date);

-- Create a trigger to automatically update the 'updated_at' timestamp
CREATE OR REPLACE FUNCTION trigger_set_timestamp()
RETURNS TRIGGER AS $$ BEGIN
  NEW.updated_at = CURRENT_TIMESTAMP;
RETURN NEW;
END;
 $$ LANGUAGE plpgsql;

-- Apply the trigger to both tables
CREATE TRIGGER set_clients_timestamp
    BEFORE UPDATE ON clients
    FOR EACH ROW
    EXECUTE FUNCTION trigger_set_timestamp();

CREATE TRIGGER set_contracts_timestamp
    BEFORE UPDATE ON contracts
    FOR EACH ROW
    EXECUTE FUNCTION trigger_set_timestamp();