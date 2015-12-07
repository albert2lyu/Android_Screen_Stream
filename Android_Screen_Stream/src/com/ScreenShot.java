package com;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.RawImage;
import com.android.ddmlib.TimeoutException;

public class ScreenShot {
	 
    public IDevice device ;
   
    /**
     * ���캯����Ĭ�ϻ�ȡ��һ���豸
     */
    public ScreenShot(){
    	AndroidDebugBridge.init(false); 
        device = this.getDevice(0);
    }
   
    /**
     * ���캯����ָ���豸���
     * @param deviceIndex �豸���
     */
    public ScreenShot(int deviceIndex){
    	AndroidDebugBridge.init(false); //
    	device = this.getDevice(deviceIndex);
    }
   
    /**
     * ֱ��ץȡ��Ļ����
     * @return ��Ļ����
     */
    public RawImage getScreenShot(){
           RawImage rawScreen = null;
           if(device!=null){
                  try {
                         rawScreen = device.getScreenshot();
                  } catch (TimeoutException e) {
                         // TODO Auto-generated catch block
                         e.printStackTrace();
                  } catch (AdbCommandRejectedException e) {
                         // TODO Auto-generated catch block
                         e.printStackTrace();
                  } catch (IOException e) {
                         // TODO Auto-generated catch block
                         e.printStackTrace();
                  }
           }else{
                  System.err.print("û���ҵ��豸");
           }
           return rawScreen;
    }
   
   
    /**
     * ��ȡͼƬbyte[]����
     * @return ͼƬbyte[]����
     */
    public byte[] getScreenShotByteData(){
    	RawImage rawScreen = getScreenShot();
        if(rawScreen != null){
        	return rawScreen.data;
        }
        return null;
    }
   
   
    /**
     * ץȡͼƬ�����浽ָ��·��
     * @param path �ļ�·��
     * @param fileName �ļ���
     */
    public void getScreenShot(String path,String fileName){
    	RawImage rawScreen = getScreenShot();
    	if(rawScreen!=null){
    		Boolean landscape = false;
    		int width2 = landscape ? rawScreen.height : rawScreen.width;
    		int height2 = landscape ? rawScreen.width : rawScreen.height;
    		BufferedImage image = new BufferedImage(width2, height2,BufferedImage.TYPE_INT_RGB);
    		if (image.getHeight() != height2 || image.getWidth() != width2) {
    			image = new BufferedImage(width2, height2,BufferedImage.TYPE_INT_RGB);
            }
    		int index = 0;
    		int indexInc = rawScreen.bpp >> 3;
    		for (int y = 0; y < rawScreen.height; y++) {
    			for (int x = 0; x < rawScreen.width; x++, index += indexInc) {
    				int value = rawScreen.getARGB(index);
    				if (landscape) image.setRGB(y, rawScreen.width - x - 1, value);
    				else image.setRGB(x, y, value);
    			}
    		}
    		try {
    			ImageIO.write((RenderedImage) image, "jpeg", new File(path + "/" + fileName + ".jpeg"));
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    	}
    }
   
    /**
     * ��ȡ�õ�device����
     * @param index �豸���
     * @return ָ���豸device����
     */
    private IDevice getDevice(int index) {
    	IDevice device = null;
        AndroidDebugBridge bridge = AndroidDebugBridge.createBridge();// ���������������鿴API���޸Ĵ˴��Ĳ���ֵ��һ��
        waitDevicesList(bridge);
        IDevice devices[] = bridge.getDevices();
            
        for (int i = 0; i < devices.length; i++) {
        	System.out.println(devices[i].toString());
        }
            
        if(devices.length < index){
        	//û�м�⵽��index���豸
            System.err.print("û�м�⵽��" + index + "���豸");
        }else{
        	if (devices.length-1>=index) {
        		device = devices[index];
        	}else{
        		device = devices[0];
        	}
        }
        return device;
    }
   
    /**
     * �ȴ�����device
     * @param bridge
     */
    private void waitDevicesList(AndroidDebugBridge bridge) {
    	int count = 0;
         while (bridge.hasInitialDeviceList() == false) {
        	try {
        		Thread.sleep(500);
        		count++;
        	} catch (InterruptedException e) {
            
        	}
        	if (count > 60) {
        		System.err.print("�ȴ���ȡ�豸��ʱ");
                break;
            }
        }
    }
}