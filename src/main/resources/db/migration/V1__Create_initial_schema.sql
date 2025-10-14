
-- Table for clients, using a single-table inheritance strategy for simplicity
CREATE TABLE clients (
                         id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                         name VARCHAR(255) NOT NULL,
                         phone VARCHAR(50),
                         email VARCHAR(255) NOT NULL UNIQUE,
                         client_type VARCHAR(20) NOT NULL CHECK (client_type IN ('PERSON', 'COMPANY')),
                         birthdate DATE,
                         company_identifier VARCHAR(50),
                         created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
                         updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL
);

-- Table for contracts
CREATE TABLE contracts (
                           id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                           client_id UUID NOT NULL ,
                           start_date DATE NOT NULL,
                           end_date DATE, -- NULL means the contract is still active
                           cost_amount NUMERIC(19, 2) NOT NULL,
                           last_modified_date TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL ,
                           CONSTRAINT fk_contract_client
                               FOREIGN KEY(client_id)
                                   REFERENCES clients(id)
                                   ON DELETE CASCADE
);


-- Create indexes for performance
CREATE INDEX idx_contracts_client_id ON contracts(client_id);
CREATE INDEX idx_contracts_end_date ON contracts(end_date);
CREATE INDEX idx_contracts_client_id_and_end_date ON contracts(client_id, end_date);