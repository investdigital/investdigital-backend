package info.investdigital.common;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.Random;


/**
 * @author ccl
 * @time 2017-10-13 14:21
 * @name QrcodeUtils
 * @desc: 二维码生成工具
 */
public class QrcodeUtils {
    private static final String CHARSET = "utf-8";
    private static final Logger logger= LoggerFactory.getLogger(QrcodeUtils.class);
    private static final String DEFAULT_QRCODE_TYPE="jpg";
    private static final int DEFAULT_QRCODE_SIZE=180;
    private static final int DEFAULT_QRCODE_LOGO_WIDTH=48;
    private static final int DEFAULT_QRCODE_LOGO_HEIGHT=48;

    //二维码颜色
    private static final int BLACK = 0xFF000000;
    //二维码颜色
    private static final int WHITE = 0xFFFFFFFF;

    public static String gen(String content,String logoPath,String destPath) throws Exception{
        return gen(content,logoPath,destPath,false);
    }
    public static String gen(String content,String destPath,boolean compressed) throws Exception{
        return gen(content,null,destPath,compressed);
    }

    public static String gen(String content,String destPath) throws Exception{
        return gen(content,null,destPath,false);
    }
    public static void gen(String content,OutputStream output) throws Exception{
        gen(content,null,output,false);
    }
    public static void gen(String content, String logoPath, OutputStream output, boolean compressed) throws Exception{
        BufferedImage image = createImage(content,logoPath,compressed);
        ImageIO.write(image,DEFAULT_QRCODE_TYPE,output);
    }
    public static String gen(String content,String logoPath,String destPath,boolean compressed) throws Exception{
        BufferedImage image=createImage(content,logoPath,compressed);
        mkdir(destPath);
        String fileName=new Random().nextInt(99999999)+"."+DEFAULT_QRCODE_TYPE;
        ImageIO.write(image, DEFAULT_QRCODE_TYPE, new File(destPath + "/" + fileName));
        return fileName;
    }
    public static String gen(String content,String logoPath,String destPath,String fileName,boolean compressed) throws Exception{
        BufferedImage image=createImage(content,logoPath,compressed);
        mkdir(destPath);
        fileName = fileName.substring(0, fileName.indexOf(".")>0?fileName.indexOf("."):fileName.length())
                + "." + DEFAULT_QRCODE_TYPE.toLowerCase();
        ImageIO.write(image, DEFAULT_QRCODE_TYPE, new File(destPath + "/" + fileName));
        return fileName;
    }

    public static String decode(String path) throws Exception{
        return decode(new File(path));
    }
    public static String decode(File file) throws Exception {
        BufferedImage image;
        image = ImageIO.read(file);
        if (image == null) {
            return null;
        }
        BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(image);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        Result result;
        Hashtable<DecodeHintType, Object> hints = new Hashtable<DecodeHintType, Object>();
        hints.put(DecodeHintType.CHARACTER_SET, CHARSET);
        result = new MultiFormatReader().decode(bitmap, hints);
        String resultStr = result.getText();
        return resultStr;
    }

    private static BufferedImage createImage(String content, String logoPath,boolean compressd) throws Exception{
        Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, CHARSET);
        hints.put(EncodeHintType.MARGIN, 1);
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, DEFAULT_QRCODE_SIZE, DEFAULT_QRCODE_SIZE,
                hints);//Exception
        int w = bitMatrix.getWidth();
        int h = bitMatrix.getHeight();

        BufferedImage image=new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
        for(int i=0;i<w;i++){
            for(int j=0;j<h;j++){
                image.setRGB(i,j,bitMatrix.get(i,j)?BLACK:WHITE);
            }
        }
        if(null==logoPath || "".equals(logoPath)){
            return image;
        }
        insertImage(image,logoPath,compressd);
        return image;
    }

    private static void insertImage(BufferedImage srcImg,String logoPath,boolean compressed) throws Exception{
        File file = new File(logoPath);
        if (!file.exists()) {
            throw new Exception("logoPath file not found.");
        }
        Image src = ImageIO.read(new File(logoPath));
        int width = src.getWidth(null);
        int height = src.getHeight(null);
        if (compressed) { // 压缩LOGO
            if (width > DEFAULT_QRCODE_LOGO_WIDTH) {
                width = DEFAULT_QRCODE_LOGO_WIDTH;
            }
            if (height > DEFAULT_QRCODE_LOGO_HEIGHT) {
                height = DEFAULT_QRCODE_LOGO_HEIGHT;
            }
            Image image = src.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics g = tag.getGraphics();
            g.drawImage(image, 0, 0, null); // 绘制缩小后的图
            g.dispose();
            src = image;
        }
        // 插入LOGO
        Graphics2D graph = srcImg.createGraphics();
        int x = (DEFAULT_QRCODE_SIZE - width) / 2;
        int y = (DEFAULT_QRCODE_SIZE - height) / 2;
        graph.drawImage(src, x, y, width, height, null);
        Shape shape = new RoundRectangle2D.Float(x, y, width, width, 6, 6);
        graph.setStroke(new BasicStroke(3f));
        graph.draw(shape);
        graph.dispose();
    }

    private static void mkdir(String path){
        File file=new File(path);
        if(!file.exists() && !file.isDirectory()){
            file.mkdir();
        }
    }
}