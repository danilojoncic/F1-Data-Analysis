package big_data.january.swing;

import big_data.january.model.Telemetry;
import big_data.january.util.GeoJsonExtractor;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.Queue;

public class TelemetryPanel extends JPanel {
    private Queue<Telemetry> telemetries;
    private Shape trackShape;

    public TelemetryPanel(Queue<Telemetry> telemetries,String trackFile) {
        this.telemetries = telemetries;
        setBackground(Color.WHITE);

        setDoubleBuffered(true);
        trackShape = GeoJsonExtractor.extractFromFile(trackFile);
        trackShape = transformShape(trackShape, getPreferredSize());
    }

    private Shape transformShape(Shape shape, Dimension panelSize) {
        Rectangle2D bounds = shape.getBounds2D();

        double centerX = bounds.getCenterX();
        double centerY = bounds.getCenterY();

        AffineTransform centerTransform = new AffineTransform();
        centerTransform.translate(-centerX, -centerY);

        Shape centeredShape = centerTransform.createTransformedShape(shape);

        Rectangle2D centeredBounds = centeredShape.getBounds2D();

        double scaleX = panelSize.getWidth() / centeredBounds.getWidth();
        double scaleY = panelSize.getHeight() / centeredBounds.getHeight();
        double scale = Math.min(scaleX, scaleY); // Preserve aspect ratio

        double scaleFactor = 48;
        scale *= scaleFactor;

        AffineTransform scaleTransform = new AffineTransform();
        scaleTransform.scale(scale*0.75, scale*1.025);

        Shape scaledShape = scaleTransform.createTransformedShape(centeredShape);

        Rectangle2D scaledBounds = scaledShape.getBounds2D();

        double targetCenterX = 360;
        double targetCenterY = 320;

        double translateX = targetCenterX - scaledBounds.getCenterX();
        double translateY = targetCenterY - scaledBounds.getCenterY();

        AffineTransform translateTransform = new AffineTransform();
        translateTransform.translate(translateX, translateY);

        return translateTransform.createTransformedShape(scaledShape);
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        Font currentFont = g2d.getFont();
        Font newFont = currentFont.deriveFont(24f); // Set font size to 24
        g2d.setFont(newFont);

        if (!telemetries.isEmpty()) {
            Telemetry firstTelemetry = telemetries.peek(); // Peek at the first element without removing it
            g2d.drawString(firstTelemetry.date(), 100, 30);
            g2d.drawString("Lap: " + (int) firstTelemetry.lapNumber(), 500, 30);
            g2d.drawString("Air Temperature: " + (int) firstTelemetry.airTemperature(), 600, 30);
            g2d.drawString("Wind Speed: " + (int) firstTelemetry.windSpeed() + ", direction: " + firstTelemetry.windDirection(), 850, 30);
        }
        drawStaticText(g2d, 600, 70,1);
        drawStaticText(g2d, 950, 70,16);
        drawStaticText(g2d, 600, 300,55);
        drawStaticText(g2d, 950, 300,44);
        drawStaticText(g2d, 600, 530,81);

        for (Telemetry telemetry : telemetries) {
            int baseX, baseY;
            int finX = (int) (telemetry.x() / 45 + 245);
            int finY = (int) (telemetry.y() / 45 + 210);
            switch ((int) telemetry.driverNumber()) {
                case 1:
                    g2d.setColor(Color.BLUE);
                    baseX = 620;
                    baseY = 70;
                    break;
                case 16:
                    g2d.setColor(Color.RED);
                    baseX = 970;
                    baseY = 70;
                    break;
                case 55:
                    g2d.setColor(Color.MAGENTA);
                    baseX = 620;
                    baseY = 300;
                    break;
                case 44:
                    g2d.setColor(Color.CYAN);
                    baseX = 970;
                    baseY = 300;
                    break;
                case 81:
                    g2d.setColor(Color.ORANGE);
                    baseX = 620;
                    baseY = 530;
                    break;
                default:
                    g2d.setColor(Color.BLACK);
                    baseX = 120; // Default position
                    baseY = 70;
                    break;
            }
            g2d.fillOval(finX-2,finY-2,20,20);
            drawDynamicData(g2d, telemetry, baseX, baseY);
        }
    }

    private void drawStaticText(Graphics2D g2d, int baseX, int baseY,int driverNumber) {
        int lineSpacing = 25;
        if(trackShape != null){
            g2d.setStroke(new BasicStroke(17));
            g2d.draw(trackShape);
        }
        g2d.setColor(Color.BLACK);
        g2d.drawString("Driver: " + (int)driverNumber, baseX, baseY);
        g2d.drawString("Throttle:", baseX, baseY + lineSpacing);
        g2d.drawString("Brake:", baseX, baseY + lineSpacing * 2);
        g2d.drawString("Interval:", baseX, baseY + lineSpacing * 3);
        g2d.drawString("Gap :", baseX, baseY + lineSpacing * 4);
        g2d.drawString("Tyre:", baseX, baseY + lineSpacing * 5);
        g2d.drawString("Gear: ", baseX, baseY + lineSpacing * 6);
        g2d.drawString("Speed: ", baseX, baseY + lineSpacing * 7);
    }

    private void drawDynamicData(Graphics2D g2d, Telemetry telemetry, int baseX, int baseY) {
        int lineSpacing = 25;

        g2d.setColor(Color.BLACK);
        g2d.drawString((int) telemetry.throttle() + "%", baseX + 100, baseY + lineSpacing);
        g2d.drawString((int) telemetry.brake() + "%", baseX + 100, baseY + lineSpacing * 2);

        String interval = String.format("%.3f", telemetry.interval());
        g2d.drawString(interval, baseX + 100, baseY + lineSpacing * 3);

        String gap = String.format("%.3f", telemetry.gapToLeader());
        g2d.drawString(gap, baseX + 100, baseY + lineSpacing * 4);
        if(telemetry.tyreCompound().equals("MEDIUM")){
            g2d.setColor(Color.YELLOW);
        }else if(telemetry.tyreCompound().equals("SOFT")){
            g2d.setColor(Color.RED);
        }else if(telemetry.tyreCompound().equals("HARD")){
            g2d.setColor(Color.GRAY);
        }
        g2d.drawString(telemetry.tyreCompound(), baseX + 100, baseY + lineSpacing * 5);
        g2d.setColor(Color.BLACK);
        g2d.drawString("" + (int)telemetry.nGear(), baseX + 100, baseY + lineSpacing * 6);
        g2d.drawString("" + (int)telemetry.speed(), baseX + 100, baseY + lineSpacing * 7);

    }


    public Queue<Telemetry> getTelemetries() {
        return telemetries;
    }

    public void setTelemetries(Queue<Telemetry> telemetries) {
        this.telemetries = telemetries;
    }
}