package UnitTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.unicamp.poo.model.Transaction;
import org.unicamp.poo.model.Wallet;
import org.unicamp.poo.model.enums.OperationType;
import org.unicamp.poo.util.MessageProvider;
import org.unicamp.poo.view.ReportView;

class ReportViewTest {

    @Test
    void rendersFinancialSummaryIncludingLoss() throws Exception {
        ReportView view = new ReportView(new MessageProvider("messages", "pt", "BR"));

        String output = capture(() -> invoke(view, "renderFinancialReport",
                new Class<?>[] {int.class, double.class, double.class, double.class, double.class,
                        double.class, double.class, double.class},
                8, 10.0, 2.0, 8.0, 50.0, 12.0, 40.0, -2.0));

        assertTrue(output.contains("8"));
        assertTrue(output.contains("50.00"));
        assertTrue(output.contains("-2.00"));
    }

    @Test
    void rendersWalletHistoryWithBuyAndSellRows() throws Exception {
        ReportView view = new ReportView(new MessageProvider("messages", "pt", "BR"));
        Wallet wallet = new Wallet(3, "Carla", "Broker");
        List<Transaction> transactions = List.of(
                new Transaction(3, LocalDate.of(2026, 10, 1), OperationType.CASH_IN, 2.0),
                new Transaction(3, LocalDate.of(2026, 10, 2), OperationType.CASH_OUT, 1.0));

        String output = capture(() -> invoke(view, "renderWalletHistoryReport",
                new Class<?>[] {Wallet.class, List.class, List.class},
                wallet, transactions, List.of(12.0, 8.0)));

        assertTrue(output.contains("Carla"));
        assertTrue(output.contains("01/10/2026"));
        assertTrue(output.contains("02/10/2026"));
        assertTrue(output.contains("12.00"));
        assertTrue(output.contains("8.00"));
    }

    @Test
    void rendersGainLossReportRowsAndAccumulatedResult() throws Exception {
        ReportView view = new ReportView(new MessageProvider("messages", "pt", "BR"));
        Wallet wallet = new Wallet(4, "Diego", "Broker");
        List<Transaction> transactions = List.of(
                new Transaction(4, LocalDate.of(2026, 10, 1), OperationType.CASH_IN, 2.0),
                new Transaction(4, LocalDate.of(2026, 10, 2), OperationType.CASH_OUT, 1.0));

        String output = capture(() -> invoke(view, "renderWalletGainLossReport",
                new Class<?>[] {Wallet.class, List.class, List.class, List.class, List.class,
                        List.class, List.class},
                wallet, transactions, List.of(5.0, 8.0), List.of(2.0, 1.0), List.of(5.0, 5.0),
                List.of(0.0, 3.0), List.of(0.0, 3.0)));

        assertTrue(output.contains("Diego"));
        assertTrue(output.contains("01/10/2026"));
        assertTrue(output.contains("02/10/2026"));
        assertTrue(output.contains("3.00"));
    }

    private static void invoke(ReportView view, String methodName, Class<?>[] parameterTypes, Object... arguments)
            throws Exception {
        Method method = ReportView.class.getDeclaredMethod(methodName, parameterTypes);
        method.setAccessible(true);
        method.invoke(view, arguments);
    }

    private static String capture(ThrowingRunnable action) throws Exception {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (PrintStream capture = new PrintStream(output, true, StandardCharsets.UTF_8)) {
            System.setOut(capture);
            action.run();
        } finally {
            System.setOut(originalOut);
        }
        return output.toString(StandardCharsets.UTF_8)
                .replaceAll("\\u001B\\[[0-9;]*m", "")
                .replace(',', '.');
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run() throws Exception;
    }
}
