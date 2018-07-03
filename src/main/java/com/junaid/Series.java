package com.junaid;

import org.apache.poi.ss.usermodel.Cell;

import java.util.ArrayList;
import java.util.List;

class Series<T>
{
	private final Class<T> typeClass;
	private List<T> data;

	/**
	 * Only String and Integer data types are supported. All entries in a series must
	 * be of the same data type.
	 */
	enum DataType
	{
		String,
		Integer
	}

	/**
	 * Creates series of given data type. Meant to be used with {@link ExcelTable}.
	 * All entries in a series must be of the same data type.
	 * @param dataType data type for given series
	 */
	Series(DataType dataType)
	{
		if ( dataType == DataType.String )
		{
			this.typeClass = (Class<T>)String.class;
		}
		else
		{
			this.typeClass = (Class<T>)Integer.class;
		}
		this.data = new ArrayList<T>();
	}

	/**
	 * Add data from given cell to series
	 * @param cell cell containing data to add (can be null)
	 */
	void put(Cell cell)
	{
		// Cell is empty
		if ( cell == null )
		{
			this.data.add(null);
			return;
		}

		// If series is of type String, then add value as a String
		if ( this.typeClass == String.class )
		{
			String value;
			if ( cell.getCellType() == Cell.CELL_TYPE_STRING )
			{
				value = cell.getStringCellValue();
			}
			else
			{
				value = String.valueOf(cell.getNumericCellValue());
			}
			this.data.add((T)value);
		}

		// If series is of type Integer, then add value as an Integer
		else
		{
			Integer value;
			if ( cell.getCellType() == Cell.CELL_TYPE_NUMERIC )
			{
				value = (int) cell.getNumericCellValue();
			}
			else
			{
				value = Integer.parseInt(cell.getStringCellValue());
			}
			this.data.add((T)value);
		}
	}

	/**
	 * Returns value at given position
	 * @param row entry number
	 * @return value at given position
	 */
	T get(int row)
	{
		if ( row > this.data.size() )
		{
			return null;
		}
		return this.data.get(row);
	}

	/**
	 * Returns the number of elements in the series
	 * @return the number of elements in the series
	 */
	int size()
	{
		return this.data.size();
	}
}