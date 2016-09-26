/*
***************************************************
**		File: Chad_Sanidad_A1.sql
**		Author: Chad Sanidad
**		Created: December 6
**		Description: All functions and procedures required for
**		CPRG300 Assignment 2.
**		The functions will return a value that the program can check to determine whether or not
**		the program will be able to communicate with the database.
**		The procedures will be used to update flags in the database, zero out the accounts when the
**		month end process is called, and export the processes to a report file.
**		Triggers will insert data into their respective tables based on access to the database.
**		
**		More information provided below:
**
**************************************************
*/
--In the case that this script is run a second time..
DROP TABLE access_log;
--Creates the auditing table access log to provide information about access on the accounts table.
CREATE TABLE access_log
(
	uname VARCHAR2(20),
	cdate DATE,
	ATYPE VARCHAR2(6)
);

--Will check the month_end table for permission to process the payrolls.
--Returns Y if it is ok to proceed or N otherwise
CREATE OR REPLACE FUNCTION payroll_process_check
RETURN VARCHAR2
AS
v_check payroll_processing.payroll%type;
BEGIN
	SELECT payroll
	INTO v_check
	FROM payroll_processing;
RETURN v_check;	
END payroll_process_check;
/		
 
--Will update payroll processing flag to n
CREATE OR REPLACE PROCEDURE update_processing_flag_n
IS
BEGIN
	UPDATE payroll_processing
	SET payroll = 'N';
END update_processing_flag_n;
/	

--Will update payroll processing flag to Y
CREATE OR REPLACE PROCEDURE update_processing_flag_y
IS
BEGIN
	UPDATE payroll_processing
	SET payroll = 'Y';
END update_processing_flag_y;
/	

--When a new line is inserted into payroll load from the bin file
-- the trigger should fire, inserting 2 rows into new transactions, 1 for accounts payable and 1 for payroll expense
CREATE OR REPLACE TRIGGER load_payroll_trg
BEFORE INSERT
	ON payroll_load
	FOR EACH ROW
DECLARE
BEGIN
	INSERT INTO NEW_TRANSACTIONS
	VALUES(wkis_seq.NEXTVAL,SYSDATE,'Credit to Accounts payable',2050,'C',:NEW.amount);
	INSERT INTO NEW_TRANSACTIONS
	VALUES(wkis_seq.CURRVAL,SYSDATE,'Debit to Payroll expense',4045,'D',:NEW.amount);
	
	:NEW.status := 'G'; -- the row has processed properly
	
EXCEPTION
	WHEN OTHERS THEN -- when there is an error processing the status is set to b
	:NEW.status := 'B';
END load_payroll_trg;
/
	
/*
When this procedure is called, it will scan through the account.
If there are any temporary accounts (revenues and expenses) it will insert 2 more rows into the new transactions table 
 to zero those accounts 
*/
CREATE OR REPLACE PROCEDURE zero_accounts
AS
	CURSOR c_account IS
	SELECT *
	FROM account
	FOR UPDATE;
	
BEGIN
	FOR r_account IN c_account LOOP
	IF(r_account.account_type_code = 'EX' AND r_account.account_balance <> 0)THEN 
		INSERT INTO new_transactions VALUES (wkis_seq.NEXTVAL, SYSDATE,'Zero Expenses', r_account.account_no,'D',r_account.account_balance);
		INSERT INTO new_transactions VALUES (wkis_seq.CURRVAL, SYSDATE, 'Expenses to OE', r_account.account_no,'C',r_account.account_balance); 
	ELSIF(r_account.account_type_code = 'RE' AND r_account.account_balance <> 0) THEN 
		INSERT INTO new_transactions VALUES (wkis_seq.NEXTVAL, SYSDATE,'Zero Revenues ', r_account.account_no,'C',r_account.account_balance);
		INSERT INTO new_transactions VALUES (wkis_seq.CURRVAL, SYSDATE, 'Revenues to OE', r_account.account_no,'D',r_account.account_balance); 
	END IF;
	END LOOP;
	COMMIT;
END zero_accounts;
/

--Will check the month_end table for permission to process month end functions.
--Returns Y if it is ok to proceed or N otherwise 
CREATE OR REPLACE FUNCTION month_end_process_check
RETURN VARCHAR2
IS
	v_month_end payroll_processing.month_end%type;
BEGIN
	SELECT month_end
	INTO v_month_end
	FROM payroll_processing;
	
	RETURN v_month_end;
END;
/
	
	--Will update month end flag to N
CREATE OR REPLACE PROCEDURE update_month_end_flag_N
IS
BEGIN
	UPDATE payroll_processing
	SET MONTH_end ='N';
END update_month_end_flag_N;
/	
--Will update month end flag to Y
CREATE OR REPLACE PROCEDURE update_month_end_flag_y
IS
BEGIN
	UPDATE payroll_processing
	SET MONTH_end ='Y';
END update_month_end_flag_y;
/	

/*
used for the reporting process.
When this procedure is called it takes in the alias of the directory, and the name of the file as parameters.
The procedure will then select everything from the new transactions table and write it to the report,
delimited by commas.
The user will also be displayed for every line in the file
*/
CREATE OR REPLACE PROCEDURE populate_file
(p_alias IN VARCHAR2, p_file_name IN VARCHAR2)
IS
	fileHandler UTL_FILE.FILE_TYPE;
	v_user VARCHAR2(20);
	CURSOR c_trans IS
	SELECT *
	FROM new_transactions;
BEGIN
	SELECT user
	INTO v_user
	FROM dual;
	dbms_output.put_line('begin');
	fileHandler := UTL_FILE.FOPEN(UPPER(p_alias),p_file_name,'W');
	
	FOR r_trans IN c_trans LOOP

	UTL_FILE.PUT_LINE(fileHandler,r_trans.transaction_no|| ' ' || r_trans.transaction_date|| ' ' || r_trans.description|| ' ' || r_trans.account_no|| ' ' || r_trans.transaction_type|| ' ' || r_trans.transaction_amount|| ' ' || v_user || ','); 
	END LOOP;
	UTL_FILE.FCLOSE(fileHandler);
	COMMIT;
END populate_file;
/

/*
The trigger will check for any sort of access into the account table
if an insert/update/delete has been done, it will log it into the access log.
*/
CREATE OR REPLACE TRIGGER audit_account_trg
BEFORE INSERT OR UPDATE OR DELETE
	ON account 
	FOR EACH ROW
DECLARE
	v_user varchar2(20);

BEGIN
	SELECT user
	INTO v_user -- get the username
	FROM dual;
	
	IF INSERTING THEN
		INSERT INTO access_log
		VALUES (v_user,sysdate,'INSERT');
	ELSIF UPDATING THEN
		INSERT INTO access_log 
		VALUES (v_user,sysdate, 'UPDATE');
	ELSIF DELETING THEN
	INSERT INTO access_log
		VALUES (v_user,sysdate,'DELETE');
	END IF;		
END audit_account_trg;
/	
