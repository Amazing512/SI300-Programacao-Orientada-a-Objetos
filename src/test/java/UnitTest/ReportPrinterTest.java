package UnitTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.unicamp.poo.view.ReportPrinter;

class ReportPrinterTest {

    @Test
    void printsPageAndRejectsPageBeyondContent() {
        BufferedImage image = new BufferedImage(300, 300, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        PageFormat pageFormat = pageFormat(300, 300);
        ReportPrinter printer = new ReportPrinter("report", List.of("first line", "second line"), false);

        try {
            assertEquals(Printable.PAGE_EXISTS, printer.print(graphics, pageFormat, 0));
            assertEquals(Printable.NO_SUCH_PAGE, printer.print(graphics, pageFormat, 1));
        } finally {
            graphics.dispose();
        }
    }

    @Test
    void keepsAnEmptyReportPrintableAsOnePage() {
        BufferedImage image = new BufferedImage(300, 300, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        ReportPrinter printer = new ReportPrinter("empty report", List.of(), false);

        try {
            assertEquals(Printable.PAGE_EXISTS, printer.print(graphics, pageFormat(300, 300), 0));
            assertEquals(Printable.NO_SUCH_PAGE, printer.print(graphics, pageFormat(300, 300), 1));
        } finally {
            graphics.dispose();
        }
    }

    @Test
    void scalesFontDownToFitLongLines() {
        BufferedImage image = new BufferedImage(300, 300, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        String longLine = "A".repeat(500);
        ReportPrinter printer = new ReportPrinter("wide report", List.of(longLine), true);

        try {
            assertEquals(Printable.PAGE_EXISTS, printer.print(graphics, pageFormat(100, 200), 0));
            assertEquals(5, graphics.getFont().getSize());
        } finally {
            graphics.dispose();
        }
    }

    private static PageFormat pageFormat(double width, double height) {
        Paper paper = new Paper();
        paper.setSize(width, height);
        paper.setImageableArea(0, 0, width, height);
        PageFormat pageFormat = new PageFormat();
        pageFormat.setPaper(paper);
        return pageFormat;
    }
}
