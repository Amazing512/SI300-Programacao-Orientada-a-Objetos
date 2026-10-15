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

/*
 * Imprime um relatório de texto usando o PrinterJob do Java.
 *
 * Como usar:
 *     ReportPrinter printer = new ReportPrinter("Nome do trabalho", linhas, false);
 *     printer.print();
 *
 * Esta classe não sabe nada de carteiras, transações ou banco de dados: ela só recebe
 * as linhas de texto já prontas e cuida de desenhá-las no papel (com várias páginas,
 * se for preciso).
 */
public class ReportPrinter implements Printable {

    // Tamanho máximo da letra. Se alguma linha não couber na largura da folha, a letra diminui.
    private static final float MAX_FONT_SIZE = 10f;
    private static final float MIN_FONT_SIZE = 5f;

    private final String jobName;
    private final List<String> lines;
    private final boolean landscape;

    // jobName: nome que aparece na fila da impressora
    // lines: as linhas do relatório, uma por item da lista
    // landscape: true = folha deitada (bom para tabelas largas), false = folha em pé
    public ReportPrinter(String jobName, List<String> lines, boolean landscape) {
        this.jobName = jobName;
        this.lines = lines;
        this.landscape = landscape;
    }

    // Abre a janela de impressão do sistema e imprime.
    // Retorna true se imprimiu e false se o usuário cancelou a janela.
    public boolean print() throws PrinterException {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setJobName(jobName);

        PageFormat pageFormat = job.defaultPage();
        pageFormat.setOrientation(landscape ? PageFormat.LANDSCAPE : PageFormat.PORTRAIT);

        // "this" é o Printable: o Java vai chamar o método print(Graphics, PageFormat, int) abaixo
        job.setPrintable(this, pageFormat);

        if (!job.printDialog()) {
            return false;
        }

        job.print();
        return true;
    }

    // O Java chama este método uma vez para cada página (e pode chamar mais de uma vez
    // para a mesma página). Por isso ele não guarda nada: tudo é calculado a partir do pageIndex.
    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) {
        Graphics2D g2 = (Graphics2D) graphics;

        double pageWidth = pageFormat.getImageableWidth();
        double pageHeight = pageFormat.getImageableHeight();

        Font font = new Font(Font.MONOSPACED, Font.PLAIN, 10).deriveFont(chooseFontSize(g2, pageWidth));
        g2.setFont(font);
        FontMetrics metrics = g2.getFontMetrics();
        int lineHeight = metrics.getHeight();

        // Reserva duas linhas no fim da folha para o número da página
        int linesPerPage = Math.max(1, (int) (pageHeight / lineHeight) - 2);
        int totalPages = Math.max(1, (lines.size() + linesPerPage - 1) / linesPerPage);

        if (pageIndex >= totalPages) {
            return NO_SUCH_PAGE;
        }

        // Move o ponto (0,0) para o canto da área onde a impressora consegue imprimir
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

    // Escolhe o tamanho da letra: 10 se tudo couber na largura, menor se alguma linha for muito larga.
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

        // 0.97 = pequena folga para a linha não encostar na borda
        float fittingSize = (float) (MAX_FONT_SIZE * availableWidth / widestLine * 0.97);
        return Math.max(MIN_FONT_SIZE, fittingSize);
    }
}
