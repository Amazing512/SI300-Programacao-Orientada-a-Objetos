package org.unicamp.poo.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.List;

public class ReportPrinter implements Printable {

    private static final float MAX_FONT_SIZE = 10f;
    private static final float MIN_FONT_SIZE = 5f;

    private final String jobName;
    private final List<String> lines;
    private final boolean landscape;

    public ReportPrinter(String jobName, List<String> lines, boolean landscape) {
        this.jobName = jobName;
        this.lines = lines;
        this.landscape = landscape;
    }

    public boolean print() throws PrinterException {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setJobName(jobName);

        PageFormat pageFormat = job.defaultPage();
        pageFormat.setOrientation(landscape ? PageFormat.LANDSCAPE : PageFormat.PORTRAIT);

        job.setPrintable(this, pageFormat);

        if (!job.printDialog()) {
            return false;
        }

        job.print();
        return true;
    }

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) {
        Graphics2D g2 = (Graphics2D) graphics;

        double pageWidth = pageFormat.getImageableWidth();
        double pageHeight = pageFormat.getImageableHeight();

        Font font = new Font(Font.MONOSPACED, Font.PLAIN, 10).deriveFont(chooseFontSize(g2, pageWidth));
        g2.setFont(font);
        FontMetrics metrics = g2.getFontMetrics();
        int lineHeight = metrics.getHeight();

        int linesPerPage = Math.max(1, (int) (pageHeight / lineHeight) - 2);
        int totalPages = Math.max(1, (lines.size() + linesPerPage - 1) / linesPerPage);

        if (pageIndex >= totalPages) {
            return NO_SUCH_PAGE;
        }

        g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
        g2.setColor(Color.BLACK);

        int firstLine = pageIndex * linesPerPage;
        int lastLine = Math.min(lines.size(), firstLine + linesPerPage);
        int y = metrics.getAscent();

        for (int index = firstLine; index < lastLine; index++) {
            g2.drawString(lines.get(index), 0, y);
            y += lineHeight;
        }

        String footer = (pageIndex + 1) + " / " + totalPages;
        int footerX = (int) pageWidth - metrics.stringWidth(footer);
        g2.drawString(footer, footerX, (int) pageHeight - metrics.getDescent());

        return PAGE_EXISTS;
    }

    private float chooseFontSize(Graphics2D g2, double availableWidth) {
        Font baseFont = new Font(Font.MONOSPACED, Font.PLAIN, 10).deriveFont(MAX_FONT_SIZE);
        FontMetrics metrics = g2.getFontMetrics(baseFont);

        int widestLine = 1;
        for (String line : lines) {
            widestLine = Math.max(widestLine, metrics.stringWidth(line));
        }

        if (widestLine <= availableWidth) {
            return MAX_FONT_SIZE;
        }

        float fittingSize = (float) (MAX_FONT_SIZE * availableWidth / widestLine * 0.97);
        return Math.max(MIN_FONT_SIZE, fittingSize);
    }
}
