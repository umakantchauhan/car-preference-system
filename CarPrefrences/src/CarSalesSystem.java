import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class CarSalesSystem extends JFrame implements ActionListener, ComponentListener
{
	public static final int CARS_COUNT = 0;
	public static final int MANUFACTURERS_COUNT = 1;
	public static final int AVERAGE_PRICE = 2;
	public static final int AVERAGE_DISTANCE = 3;
	public static final int AVERAGE_AGE = 4;
	public static final int NAME_COUNT = 5;
	

	private String file;
	private boolean carsUpdated = false;
	private Vector registeredListeners = new Vector();
	private CarsCollection carCollection;
	private JPanel topPanel = new JPanel(new BorderLayout());
	private JPanel titlePanel = new JPanel(new GridLayout(2, 1));
	private JLabel pictureLabel = new JLabel();
	private JLabel carCoLabel = new JLabel("CAR PREFERENCE SYSTEM", JLabel.CENTER);
	private JLabel salesSysLabel = new JLabel("Search cars available or add your own car", JLabel.CENTER);
	private JTabbedPane theTab = new JTabbedPane(JTabbedPane.TOP);
	private JMenuBar menuBar = new JMenuBar();
	private JMenu fileMenu = new JMenu("Exit program");
	private JMenuItem aboutItem = new JMenuItem("");
	private JMenuItem exitItem = new JMenuItem("Exit");
	private WindowCloser closer = new WindowCloser();

	public CarSalesSystem(String f)
	{
		super("Car Sales");

		addWindowListener(closer);
		addComponentListener(this);
		setSize(800, 480);
		Container c = getContentPane();
		carCollection = new CarsCollection();

		file = f;

		try
		{
			carCollection.loadCars(file);
		}
		catch (java.io.FileNotFoundException exp)
		{
			System.out.println("The data file, 'cars.dat' doesn't exist. Plase create an empty file named 'cars.dat'");
			System.exit(0);
		}
		catch (java.io.EOFException exp){}
		catch (java.io.IOException exp)
		{
			System.out.println("The data file, 'cars.dat' is possibly corrupted. Please delete it and create a new empty data file named cars.dat");
			System.exit(0);
		}
		catch (Exception exp)
		{
			System.out.println("There was an error loading 'cars.dat'. Try deleting and creating a new empty file named 'cars.dat'");
			System.exit(0);
		}
		String currentFont = carCoLabel.getFont().getName();
		carCoLabel.setFont(new Font(currentFont, Font.BOLD, 26));
		salesSysLabel.setFont(new Font(currentFont, Font.PLAIN, 16));

		menuBar.add(fileMenu);
		fileMenu.add(aboutItem);
		fileMenu.add(exitItem);
		aboutItem.addActionListener(this);


		setJMenuBar(menuBar);

		pictureLabel.setIcon(new ImageIcon("car.png"));
		titlePanel.add(carCoLabel);
		titlePanel.add(salesSysLabel);
		topPanel.add(pictureLabel, "East");
		topPanel.add(titlePanel, "West");
	}

	

	
	
	public void addCarUpdateListener(Object listener)
	{
		if (!(listener == null))
			registeredListeners.add(listener);
	}

	
	public int addNewCar(Car c)
	{
		return carCollection.addCar(c);
	}

	public void closing()
	{
		boolean ok;

		if (carsUpdated)
		{
			do
			{
				try
				{
					carCollection.saveCars(file);
					ok = true;
				}
				catch (java.io.IOException exp)
				{
					int result = JOptionPane.showConfirmDialog(this, "The data file could not be written, possibly because you don't have access to this location.\nIf you chose No to retry you will lose all car data from this session.\n\nWould you like to reattempt saving the data file?", "Problem saving data", JOptionPane.YES_NO_OPTION);

					if (result == JOptionPane.YES_OPTION)
						ok = false;
					else
						ok = true;
				}
			}
			while (!ok);
		}

		System.exit(0);
	}

	public void componentHidden(ComponentEvent ev) {}

	public void componentMoved(ComponentEvent ev) {}

	
	public void componentResized(ComponentEvent ev)
	{
		if (this == ev.getComponent())
		{
                    getContentPane().setBackground(Color.WHITE);
			Dimension size = getSize();

			if (size.height < 1000)
				size.height = 800;
			if (size.width < 1000)
				size.width = 1000;

			setSize(size);
		}
	}
	public void componentShown(ComponentEvent ev) {}

	public static double[] convertToRange(String s)
	{
		String[] parts = s.trim().split("-");
		double[] bounds = new double[2];

		try
		{
			if (parts.length == 1)
			{
				String c = s.substring(s.length() - 1);

				
				if (c.equals("+"))
				{
					
					bounds[0] = Double.parseDouble(s.substring(0, s.length() - 1));
					
					bounds[1] = -1;
				}
				else
				{
					
					bounds[0] = Double.parseDouble(s);
					bounds[1] = bounds[0];
				}
			}
			
			else if(parts.length == 2)
			{
				bounds[0] = Double.parseDouble(parts[0]);
				bounds[1] = Double.parseDouble(parts[1]);
				if (bounds[0] > bounds[1])
				{
					
					bounds[0] = -1;
					bounds[1] = -1;
				}
			}
		}
		catch (NumberFormatException exp)
		{
			
			bounds[0] = -1;
			bounds[1] = -1;
		}

		return bounds;
	}

	
	public Car[] getAllCars()
	{
		return carCollection.getAllCars();
	}


	
	public boolean getCarsUpdated()
	{
		return carsUpdated;
	}

	
	public double getStatistics(int type)
	{
		double result = 0;

		if (type == CARS_COUNT)
		{
			result	= carCollection.carsCount();
		}
		else if (type == MANUFACTURERS_COUNT)
		{
			result = carCollection.manufacturerCount();
		}
		else if (type == AVERAGE_PRICE)
		{
			result = carCollection.getAveragePrice();
		}
		else if (type == AVERAGE_DISTANCE)
		{
			result = carCollection.getAverageDistance();
		}
		else if (type == AVERAGE_AGE)
		{
			result = carCollection.getAverageAge();
		}

		return result;
	}

	
	public static void main(String[] args)
	{
		CarSalesSystem carSales = new CarSalesSystem("cars.dat");
		carSales.setVisible(true);
	}

	
	public Car[] search(int minAge, int maxAge)
	{
		return carCollection.search(minAge, maxAge);
	}

	
	public Car[] search(int minPrice, int maxPrice, double minDistance, double maxDistance)
	{
		return carCollection.search(minPrice, maxPrice,  minDistance, maxDistance);
	}

	
	public void setCarsUpdated()
	{
		carsUpdated = true;

		for (int i = 0; i < registeredListeners.size(); i++)
		{
			Class[] paramType = {CarUpdateEvent.class};
			Object[] param = {new CarUpdateEvent(this)};

			try
			{
				
				java.lang.reflect.Method callingMethod = registeredListeners.get(i).getClass().getMethod("carsUpdated", paramType);
				
				callingMethod.invoke(registeredListeners.get(i), param);
			}
			catch (NoSuchMethodException exp)
			{
				System.out.println("Warning, 'public carsUpdated(CarEvent)' method does not exist in " + registeredListeners.get(i).getClass().getName() + ". You will not receive any car update events");
			}
			catch (IllegalAccessException exp)
			{
				System.out.println("Warning, the 'public carUpdated(CarEvent)' method couldn't be called for unknown reasons, You will not receive any car update events");
			}
			catch (Exception exp){}
		}
	}
	
	public static Car[] vectorToCar(Vector v)
	{
		Car[] ret = new Car[v.size()];

		for (int i = 0; i < v.size(); i++)
		{
			ret[i] = (Car)v.elementAt(i);
		}

		return ret;
	}

    @Override
    public void actionPerformed(ActionEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

	class WindowCloser extends WindowAdapter
	{
		
		public void windowClosing(WindowEvent ev)
		{
			closing();
		}
	}
}