ALTER TABLE credit_application 
ADD COLUMN IF NOT EXISTS loan_purpose VARCHAR(255);

ALTER TABLE credit_application 
ADD COLUMN IF NOT EXISTS decision_date TIMESTAMP WITH TIME ZONE;

UPDATE credit_application 
SET loan_purpose = 'Не указано' 
WHERE loan_purpose IS NULL;

ALTER TABLE credit_application 
ALTER COLUMN loan_purpose SET NOT NULL;