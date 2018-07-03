package com.junaid;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.*;

public class CodeTest
{
	/**
	 * Data series for Data1.xlsx and Data2.xlsx
	 */
	private enum DataSeries
	{
		numberSetOne,
		numberSetTwo,
		wordSetOne
	}

	/**
	 * TopBloc code test
	 * @param args unused
	 * @throws IOException if Excel files can not be read
	 */
	public static void main(String[] args) throws IOException
	{
		// Step 1: Download workbooks
		// 	workbooks saved in resources folder

		// Step 2: Use Maven to manage any libraries
		//  see pom

		// Step 3: Parse workbooks
		ExcelTableFormat format = new ExcelTableFormat();
		if ( !format.add(DataSeries.numberSetOne.toString(), Series.DataType.Integer) ||
			 !format.add(DataSeries.numberSetTwo.toString(), Series.DataType.Integer) ||
			 !format.add(DataSeries.wordSetOne.toString(), Series.DataType.String)	  )
		{
			throw new RuntimeException("Error defining format");
		}

		ClassLoader classLoader = new CodeTest().getClass().getClassLoader();
		File file1 = new File(classLoader.getResource("Data1.xlsx").getFile());
		File file2 = new File(classLoader.getResource("Data2.xlsx").getFile());
		ExcelTable Data1 = new ExcelTable(file1, format);
		ExcelTable Data2 = new ExcelTable(file2, format);

		// Step 4: Multiply numberSetOne
		Integer[] numberSetOneProduct = multiplySeries(Data1, Data2, DataSeries.numberSetOne);

		// Step 5: Divide numberSetTwo
		Integer[] numberSetTwoQuotient = divideSeries(Data1, Data2, DataSeries.numberSetTwo);

		// Step 6: Concatenate wordSetOne
		String[] wordSetOneConcatenation = concatenateSeries(Data1, Data2, DataSeries.wordSetOne);

		// Step 7: Create and post JSON request
		JSONObject json = new JSONObject();
		json.put("id", "j.begawala@gmail.com");
		json.put("numberSetOne", new JSONArray(Arrays.asList(numberSetOneProduct)));
		json.put("numberSetTwo", new JSONArray(Arrays.asList(numberSetTwoQuotient)));
		json.put("wordSetOne", new JSONArray(Arrays.asList(wordSetOneConcatenation)));

		HttpClient httpClient = HttpClients.createDefault();
		HttpPost request = new HttpPost("http://34.239.125.159:5000/challenge");
		request.addHeader("content-type", "application/json");
		request.setEntity(new StringEntity(json.toString(), "UTF-8"));

		HttpResponse response;
		try
		{
			response = httpClient.execute(request);
			int responseCode = response.getStatusLine().getStatusCode();
			if ( responseCode == 200 )
			{
				System.out.println("Posted successfully");
			}
			else
			{
				System.out.println("Failed to post (status code " + response +")");
			}
		}
		catch(HttpHostConnectException e)
		{
			System.out.println("An error occurred while attempting to connect: " + e.getMessage() +")");
		}
	}

	/**
	 * Performs element-wise multiplication of the given series in two tables. The series
	 * must have the same number of elements in both tables.
	 * @param table1 first data source
	 * @param table2 second data source
	 * @param s series to multiply
	 * @return array of products
	 */
	private static Integer[] multiplySeries(ExcelTable table1, ExcelTable table2, DataSeries s)
	{
		String series = s.toString();
		int size = table1.size(series);
		if ( size != table2.size(series) )
		{
			throw new RuntimeException("Element count mismatch for " + series);
		}
		Integer[] product = new Integer[size];
		for ( int i = 0; i < size; i++ )
		{
			product[i] = table1.getIntegerValue(series, i) * table2.getIntegerValue(series, i);
		}
		return product;
	}

	/**
	 * Performs element-wise division of the given series in two tables (table1 / table2).
	 * The series must have the same number of elements in both tables.
	 * @param table1 first data source
	 * @param table2 second data source
	 * @param s series to divide
	 * @return array of quotients
	 */
	private static Integer[] divideSeries(ExcelTable table1, ExcelTable table2, DataSeries s)
	{
		String series = s.toString();
		int size = table1.size(series);
		if ( size != table2.size(series) )
		{
			throw new RuntimeException("Element count mismatch for " + series);
		}
		Integer[] quotient = new Integer[size];
		for ( int i = 0; i < size; i++ )
		{
			quotient[i] = table1.getIntegerValue(series, i) / table2.getIntegerValue(series, i);
		}
		return quotient;
	}

	/**
	 * Performs element-wise concatenation of the given series in two tables (table1 + space + table2).
	 * The series must have the same number of elements in both tables.
	 * @param table1 first data source
	 * @param table2 second data source
	 * @param s series to concatenate
	 * @return array of concatenated strings
	 */
	private static String[] concatenateSeries(ExcelTable table1, ExcelTable table2, DataSeries s)
	{
		String series = s.toString();
		int size = table1.size(series);
		if ( size != table2.size(series) )
		{
			throw new RuntimeException("Element count mismatch for " + series);
		}
		String[] concatenation = new String[size];
		for ( int i = 0; i < size; i++ )
		{
			concatenation[i] = table1.getStringValue(series, i) + " " + table2.getStringValue(series, i);
		}
		return concatenation;
	}
}
