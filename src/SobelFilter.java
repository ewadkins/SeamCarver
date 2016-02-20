import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;

public class SobelFilter {

	public static BufferedImage filter(BufferedImage bufferedImage) {
		//bufferedImage = grayOut(bufferedImage);
		int[][] data = new int[bufferedImage.getWidth()][bufferedImage.getHeight()];
		BufferedImage result = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_RGB);
		int[][] pixelMatrix = new int[3][3];
		for (int i = 1; i < bufferedImage.getWidth() - 1; i++) {
			for (int j = 1; j < bufferedImage.getHeight() - 1; j++) {

				pixelMatrix[0][0] = (bufferedImage.getRGB(i - 1, j - 1) >> 16 ) & 0xFF;
				pixelMatrix[0][1] = (bufferedImage.getRGB(i - 1, j) >> 16 ) & 0xFF;
				pixelMatrix[0][2] = (bufferedImage.getRGB(i - 1, j + 1) >> 16 ) & 0xFF;
				pixelMatrix[1][0] = (bufferedImage.getRGB(i, j - 1) >> 16 ) & 0xFF;
				pixelMatrix[1][2] = (bufferedImage.getRGB(i, j + 1) >> 16 ) & 0xFF;
				pixelMatrix[2][0] = (bufferedImage.getRGB(i + 1, j - 1) >> 16 ) & 0xFF;
				pixelMatrix[2][1] = (bufferedImage.getRGB(i + 1, j) >> 16 ) & 0xFF;
				pixelMatrix[2][2] = (bufferedImage.getRGB(i + 1, j + 1) >> 16 ) & 0xFF;

				int edge = (int) convolution(pixelMatrix);
				result.setRGB(i, j, (edge << 16 | edge << 8 | edge));
				data[i][j] = (edge << 16 | edge << 8 | edge);
			}
		}
		
		/*for (int[] row : data) {
			for (int i : row) {
				System.out.print(i);
				System.out.print("\t");
			}
			System.out.print("\n");
		}*/
		return result;
	}

	private static double convolution(int[][] pixelMatrix) {
		int gy = (pixelMatrix[0][0] * -1) + (pixelMatrix[0][1] * -2) + (pixelMatrix[0][2] * -1) + (pixelMatrix[2][0])
				+ (pixelMatrix[2][1] * 2) + (pixelMatrix[2][2] * 1);
		int gx = (pixelMatrix[0][0]) + (pixelMatrix[0][2] * -1) + (pixelMatrix[1][0] * 2) + (pixelMatrix[1][2] * -2)
				+ (pixelMatrix[2][0]) + (pixelMatrix[2][2] * -1);
		return Math.sqrt(Math.pow(gy, 2) + Math.pow(gx, 2));
	}
	
	private static BufferedImage grayOut(BufferedImage bufferedImage) {
		BufferedImage result = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_RGB);
		ColorConvertOp colorConvert = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
		colorConvert.filter(bufferedImage, result);

		return result;
	}
	
}
