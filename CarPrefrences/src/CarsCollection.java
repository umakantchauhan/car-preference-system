import java.util.*;
import java.io.*;

public class CarsCollection
{
	
	public static final int NO_ERROR = 0;
	
	public static final int CARS_MAXIMUM_REACHED = 1;
	
	public static final int MANUFACTURERS_MAXIMUM_REACHED = 2;

	private final int maxManufacturers = 20;
	private final int maxCars = 20;

	private Manufacturer[] manufacturer = new Manufacturer[0];

	public CarsCollection(){}

	public CarsCollection(Manufacturer man)
	{
		addManufacturer(man);
	}

	
	public int addCar(Car c)
	{
		Manufacturer man;
		String name = c.getManufacturer();
		int index = -1;
		int result = NO_ERROR;

		for (int i = 0; i < manufacturer.length; i++)
		{
			if (manufacturer[i].getManufacturerName().equalsIgnoreCase(name))
				index = i;
		}

		if (index == -1)
		{
			if (manufacturer.length < maxManufacturers)
			{
				man = new Manufacturer(name, c);
				addManufacturer(man);
			}
			else
				result = MANUFACTURERS_MAXIMUM_REACHED;
		}
		else
		{
			if (manufacturer[index].carCount() < maxCars)
				manufacturer[index].addCar(c);
			else
				result = CARS_MAXIMUM_REACHED;
		}

		return result;
	}

	
	private void addManufacturer(Manufacturer man)
	{
		manufacturer = resizeArray(manufacturer, 1);
		manufacturer[manufacturer.length - 1] = man;
	}

	
	public int carsCount()
	{
		int count = 0;

            for (Manufacturer manufacturer1 : manufacturer) {
                count += manufacturer1.carCount();
            }

		return count;
	}

	
	public int manufacturerCount()
	{
		return manufacturer.length;
	}

	
	public Car[] getAllCars()
	{
		Vector result = new Vector();
		Car[] car;
            for (Manufacturer manufacturer1 : manufacturer) {
                car = manufacturer1.getAllCars();
                    for (Car car1 : car) {
                        result.addElement(car1);
                    }
            }

		return CarSalesSystem.vectorToCar(result);
	}

	public Manufacturer[] getAllManufacturers()
	{
		return manufacturer;
	}

	
	public double getAverageAge()
	{
		Car[] car;
		double result = 0;
		int count = 0;

            for (Manufacturer manufacturer1 : manufacturer) {
                car = manufacturer1.getAllCars();
                    for (Car car1 : car) {
                        result += car1.getAge();
                        count++;
                    }
            }
		if (count == 0)
			return 0;
		else
			return (result / count);
	}

	public double getAverageDistance()
	{
		Car[] car;
		double result = 0;
		int count = 0;

            for (Manufacturer manufacturer1 : manufacturer) {
                car = manufacturer1.getAllCars();
                    for (Car car1 : car) {
                        result += car1.getKilometers();
                        count++;
                    }
            }
		if (count == 0)
			return 0;
		else
			return (result / count);
	}

	
	public double getAveragePrice()
	{
		Car[] car;
		double result = 0;
		int count = 0;

            for (Manufacturer manufacturer1 : manufacturer) {
                car = manufacturer1.getAllCars();
                    for (Car car1 : car) {
                        result += car1.getPrice();
                        count++;
                    }
            }
		if (count == 0)
			return 0;
		else
			return (result / count);
	}
	
	public void loadCars(String file) throws IOException, ClassNotFoundException
	{

            try (ObjectInputStream inp = new ObjectInputStream(new FileInputStream(file))) {
                manufacturer = (Manufacturer[])inp.readObject();
            }
	}

	
	private Manufacturer[] resizeArray(Manufacturer[] inArray, int extendBy)
	{
		Manufacturer[] result = new Manufacturer[inArray.length + extendBy];

            System.arraycopy(inArray, 0, result, 0, inArray.length);

		return result;
	}

	
	public void saveCars(String file) throws IOException
	{
		int flag = 0;
		int items = manufacturer.length;
		Manufacturer temp;

		if (manufacturer.length > 0)
		{
			do
			{
				flag = 0;
				for (int i = 0; i < items; i++)
				{
					if (i + 1 < items)
					{
						if (manufacturer[i].getManufacturerName().compareTo(manufacturer[i + 1].getManufacturerName()) > 0)
						{
							temp = manufacturer[i];
							manufacturer[i] = manufacturer[i + 1];
							manufacturer[i + 1] = temp;
							flag++;
						}
					}
				}
			}
			while (flag > 0);

			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));

			out.writeObject(manufacturer);
			out.close();
		}
	}

	
	public Car[] search(int minPrice, int maxPrice, double minDistance, double maxDistance)
	{
		Vector result = new Vector();
		int price;
		double distance;
		Car[] car;
		car = getAllCars();

            for (Car car1 : car) {
                price = car1.getPrice();
                distance = car1.getKilometers();
                if (price >= minPrice && price <= maxPrice && distance >= minDistance && distance <= maxDistance) {
                    result.addElement(car1);
                }
            }

		return CarSalesSystem.vectorToCar(result);
	}

	
	public Car[] search(int minAge, int maxAge)
	{
		Car[] car;
		car = getAllCars();
		Vector result = new Vector();
		if (maxAge == -1)
		{
                    for (Car car1 : car) {
                        if (car1.getAge() >= minAge) {
                            result.addElement(car1);
                        }
                    }
		}
		else
		{
                    for (Car car1 : car) {
                        if (car1.getAge() >= minAge && car1.getAge() <= maxAge) {
                            result.addElement(car1);
                        }
                    }
		}

		return CarSalesSystem.vectorToCar(result);
	}
}