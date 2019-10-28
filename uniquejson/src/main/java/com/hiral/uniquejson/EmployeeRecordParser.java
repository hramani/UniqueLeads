package com.hiral.uniquejson;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hiral.uniquejson.model.EmployeeRecord;
import com.hiral.uniquejson.model.Leads;

/**
 * handles parsing raw json
 * 
 * @author hiralr
 *
 */
public class EmployeeRecordParser {

	String fileName;

	public EmployeeRecordParser(String fileName) {
		this.fileName = fileName;
	}

	public void parseEmployeeFile() throws IOException {
		Gson gson = new Gson();
		// loading the employee record
		Leads leads = gson.fromJson(new FileReader(this.fileName), Leads.class);
		long before = System.currentTimeMillis();
		// filtering the files
		Leads uniqueLeads = getUniqueEmployeesV1(leads.getLeads());
		long after = System.currentTimeMillis();
		System.err.println("Total time taken in ms: " + (after - before) + " ms");
		// saving the files
		String outputFile = System.getProperty("user.dir") + "\\uniqueleads.json";
		System.out.println("Saving output file to: " + outputFile);
		Writer writer = new FileWriter(outputFile);
		new GsonBuilder().setPrettyPrinting().create().toJson(uniqueLeads, Leads.class, writer);
		writer.close();
	}

	/**
	 * here we store id and email as key
	 * 
	 * @param leads
	 * @return
	 */
	private Leads getUniqueEmployeesV1(EmployeeRecord[] leads) {
		HashMap<String, EmployeeRecord> uniqueEmployeeMap = new HashMap<String, EmployeeRecord>();
		HashSet<EmployeeRecord> uniqueEmployeeRecordSet = new HashSet<EmployeeRecord>();
		boolean addRecord = true;
		for (EmployeeRecord newEmployeeRecord : leads) {
			System.out.println("*** Starting to add new element ***");
			addRecord = true;
			String newEmployeeRecordId = newEmployeeRecord.get_id();
			String newEmployeeRecordEmail = newEmployeeRecord.getEmail();
			System.out.println("checking if element with id: " + newEmployeeRecordId + " or email: "
					+ newEmployeeRecordEmail + " exists");
			if (uniqueEmployeeMap.containsKey(newEmployeeRecordId)
					| uniqueEmployeeMap.containsKey(newEmployeeRecordEmail)) {
				
				EmployeeRecord existingEmployeeRecord1 = uniqueEmployeeMap.containsKey(newEmployeeRecordId)
						? uniqueEmployeeMap.get(newEmployeeRecordId)
						: uniqueEmployeeMap.get(newEmployeeRecordEmail);
				
				EmployeeRecord existingEmployeeRecord2 = uniqueEmployeeMap.containsKey(newEmployeeRecordEmail)
						? uniqueEmployeeMap.get(newEmployeeRecordEmail)
						: uniqueEmployeeMap.get(newEmployeeRecordId);
				//handling corner case where record 1 2 and 3 have overlapping with 3 overlapping on 1 and 2
				
				if(existingEmployeeRecord1!=null & existingEmployeeRecord2!=null) {
					//here we will try to remove to docs one that matched with email and another that matched with id
					boolean addRecord1 = handleExistingRecord(uniqueEmployeeMap, uniqueEmployeeRecordSet, newEmployeeRecord,
							existingEmployeeRecord1);
					boolean addRecord2 = handleExistingRecord(uniqueEmployeeMap, uniqueEmployeeRecordSet, newEmployeeRecord,
							existingEmployeeRecord2);
					addRecord = addRecord1 | addRecord2;
				} else {
					addRecord = handleExistingRecord(uniqueEmployeeMap, uniqueEmployeeRecordSet, newEmployeeRecord,
							existingEmployeeRecord1);
				}
			}
			
			if(addRecord) {
				System.out.println("Adding new employee record with id: " + newEmployeeRecordId + " and email: "
						+ newEmployeeRecordEmail);
				// add new employees email and id
				uniqueEmployeeMap.put(newEmployeeRecordId, newEmployeeRecord);
				uniqueEmployeeMap.put(newEmployeeRecordEmail, newEmployeeRecord);
				uniqueEmployeeRecordSet.add(newEmployeeRecord);
			}
			
		}

		Leads uniqueLeadsObj = new Leads();
		// creating array of map size
		EmployeeRecord[] employeeRecords = new EmployeeRecord[uniqueEmployeeRecordSet.size()];
		employeeRecords = uniqueEmployeeRecordSet.toArray(employeeRecords);
		uniqueLeadsObj.setLeads(employeeRecords);
		return uniqueLeadsObj;
	}

	private boolean handleExistingRecord(HashMap<String, EmployeeRecord> uniqueEmployeeMap,
			HashSet<EmployeeRecord> uniqueEmployeeRecordSet, EmployeeRecord newEmployeeRecord,
			EmployeeRecord existingEmployeeRecord) {
		boolean addRecord;
		String existingEmployeeId = existingEmployeeRecord.get_id();
		String existingEmployeeEmail = existingEmployeeRecord.getEmail();
		System.out.println("Existing element with: " + existingEmployeeId + " and email: "
				+ existingEmployeeEmail + " exists");
		if (existingEmployeeRecord.getEntryDate().compareTo(newEmployeeRecord.getEntryDate()) > 0) {
			System.out.println("Existing element with: " + existingEmployeeId + " and email: "
					+ existingEmployeeEmail + " exists has newer date: " + existingEmployeeRecord.getEntryDate()
					+ " compared to new employee record date: " + newEmployeeRecord.getEntryDate());
			// we have the correct id
			addRecord = false;
		} else {
			System.out.println("Existing element with: " + existingEmployeeId + " and email: "
					+ existingEmployeeEmail + " exists has older date: " + existingEmployeeRecord.getEntryDate()
					+ " compared to new employee record date: " + newEmployeeRecord.getEntryDate());
			// remove existing records email and id
			uniqueEmployeeMap.remove(existingEmployeeId);
			uniqueEmployeeMap.remove(existingEmployeeEmail);
			uniqueEmployeeRecordSet.remove(existingEmployeeRecord);
			addRecord = true;
		}
		return addRecord;
	}
}
