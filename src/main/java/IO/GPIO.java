package IO;

import Errors.IllegalPinModeException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Acer on 11-5-2016.
 *
 * @author David de Prez
 * @version 1.0
 */
public class GPIO {
    private static Object defaultGpio;

    public static void setDefaultGpio(Object defaultGpio) {
        GPIO.defaultGpio = defaultGpio;
    }

    /**
     * Read the status of the given Gpio pin. This method reflect to de class in the default package
     *
     * @param a The kernel ID of the Gpio pin.
     * @param v The value the set the Gpio pin
     */
    private void iowrite(int a, int v) {
        try {
            Method method = defaultGpio.getClass().getMethod("iowrite", int.class, int.class);
            method.invoke(defaultGpio, a, v);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Read the status of the given Gpio pin. This method reflect to de class in the default package
     *
     * @param a The kernel ID of the given Gpio pin.
     */
    private int ioread(int a) {
        try {
            Method method = defaultGpio.getClass().getMethod("ioread", int.class);
            return (Integer) method.invoke(defaultGpio, a);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * To de-initialise the Gpio pin. Must be called when close program or when the Gpio pins are no longer be used.
     */
    public void deinit() {
        try {
            Method method = defaultGpio.getClass().getMethod("iodeinit");
            method.invoke(defaultGpio);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * With this method you can change the value of the given pin. The given pin must be a output pin. The value
     * of the pin will be 1 or 0.
     *
     * @param pin      The pin to change the value
     * @param isHeight The value. If set to true, the pin goes to high (1). If set to false, the pin goes to low (0)
     */
    public void setPin(Pin pin, boolean isHeight) {
        //check if the pin has the right mode
        if (pin.isOutput) {
            //get kernel ID; if value > 0 -> set pin to 1, else set pin to 0
            iowrite(pin.ID, isHeight ? 1 : 0);
        } else {
            //throw exception
            throw new IllegalPinModeException();
        }
    }

    /**
     * Read the value of the given pin. Pin must be an input pin.
     *
     * @param pin A digital input pin to read from
     * @return false if pin is low, true if pin is high
     */
    public boolean getPin(Pin pin) {
        if (!pin.isOutput) {
            return ioread(pin.ID) != 0;
        }

        throw new IllegalPinModeException();
    }

    /**
     * List of all usable Gpio pins, save with the right kernel ID and isOutput (input or output)
     */
    public enum Pin {
        PB31(false, 95),//MUX
        PB30(false, 94),//DIAL
        PB21(false, 85),//DIAL
        PB20(false, 84),//FREE
        PB19(false, 83),//DREQ
        PB17(true, 81),//SHIFT clk
        PB16(true, 80),//SHIFT serial
        PA28(true, 60),//SHIFT latch
        PA27(true, 59),//MUX 0
        PA26(true, 58),//MUX 1
        PA25(true, 57),//MUX 2
        PA22(true, 54),//LCD r/w
        PA11(true, 43),//LCD rs
        PA10(true, 42),//PREV LED
        PA9(true, 42),//PLAY LED
        PA7(true, 39),//NEXT LED
        PA6(true, 38);//PWR LED


        /**
         * Meaning of bits:
         * ab
         * a -> output(0)/input(1)
         * b -> GPIO(0)/SPI(1)
         */
        private final boolean isOutput;
        private final int ID;

        Pin(boolean isOutput, int ID) {
            this.isOutput = isOutput;
            this.ID = ID;
        }
    }
}
