/**
 * 
 */


import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

/**
 * @author dlegland
 *
 */
public class Game_Of_Life implements PlugIn {

	/* (non-Javadoc)
	 * @see ij.plugin.PlugIn#run(java.lang.String)
	 */
	@Override
	public void run(String arg)
	{
		ImagePlus imagePlus = IJ.getImage();
		ImageProcessor image = imagePlus.getProcessor();
		
		// create the dialog
		GenericDialog gd = new GenericDialog("Game Of Life");
		gd.addNumericField("Iterations", 200, 0);
		gd.addCheckbox("Create Stack", true);
		gd.showDialog();
		
		if (gd.wasCanceled())
			return;
		
		// parse current parameters
		int iterationNumber = (int) gd.getNextNumber();
		boolean createStack = gd.getNextBoolean();
		
		if (createStack)
		{
			// Concatenate all results in a single stack
			ImageStack stack = ImageStack.create(image.getWidth(), image.getHeight(), iterationNumber+1, 8);
			
			// keep initial state in stack
			ImageProcessor result = image.duplicate();
			stack.setProcessor(image, 1);
			
			// process iterations
			for (int i = 0; i < iterationNumber; i++)
			{
				result = iterateGameOfLife(result);
				stack.setProcessor(result, i + 2);
			}
			
			// display result
			String newName = imagePlus.getShortTitle() + "-life" + iterationNumber;
			ImagePlus stackPlus = new ImagePlus(newName, stack);
			stackPlus.show();
		}
		else
		{
			// apply the iterations, and display the result
			ImageProcessor result = image.duplicate();
			for (int i = 0; i < iterationNumber; i++)
			{
				result = iterateGameOfLife(result);
			}
			ImagePlus resultPlus = new ImagePlus(imagePlus.getShortTitle() + "It", result);
			resultPlus.show();
		}
	}

	public static final ImageProcessor iterateGameOfLife(ImageProcessor image)
	{
		int sizeX = image.getWidth();
		int sizeY = image.getHeight();
		
		ImageProcessor result = new ByteProcessor(sizeX, sizeY);
		
		for (int y = 0; y < sizeY; y++)
		{
			for (int x = 0; x < sizeX; x++)
			{
				// Count the number of neighbors
				int count = 0;
				for (int dy = -1; dy <= +1; dy++)
				{
					int y2 = (y + dy + sizeY) % sizeY;
					for (int dx = -1; dx <= +1; dx++)
					{
						int x2 = (x + dx + sizeX) % sizeX;
						if (image.get(x2, y2) > 0)
						{
							count++;
						}
					}
				}
				
				// Determines the state of cell at next iteration
				boolean alive;
				if (image.get(x, y) > 0)
				{
					// do not count current cell
					count--;
					// should current cell stay alive?
					alive = count == 2 || count == 3;
				}
				else
				{
					alive = count == 3;
				}
				
				result.set(x, y, alive ? 255 : 0);
			}
		}
		return result;
	}

}
