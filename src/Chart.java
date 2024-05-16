import java.io.*;

//Definition of Chart class for LangChart solution
//Charts must be fixed row/column dimensions,
//with each row containing the same number of columns
public class Chart {
    private String name;
    private String[][] data;
    private int rows, columns;

    Chart(File chartPath) {
        name = chartPath.getName().substring(0, chartPath.getName().lastIndexOf("."));
        //Get number of rows in chart
        try {
            BufferedReader reader = new BufferedReader(new FileReader(chartPath));
            rows = 0;
            while (reader.readLine() != null) rows++;
            System.out.println("Rows: " + rows);
            reader.close();
        } catch (FileNotFoundException e) { //Reader could not open file
            throw new RuntimeException(e);
        } catch (IOException e) { //Could not read new line
            throw new RuntimeException(e);
        }

        try {
            BufferedReader scn = new BufferedReader(new FileReader(chartPath));
            String[] rowSplit;
            
            rowSplit = scn.readLine().split(",");
            columns = rowSplit.length;
            System.out.println("Columns: " + columns);
            data = new String[rows][columns];
            data[0] = rowSplit;

            for (int i = 1; i < rows; i++) {
                rowSplit = scn.readLine().split(",");
                data[i] = rowSplit;
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    String get(int row, int col) {
        return data[row][col];
    }
    String getName() { return name; }
    int getRows() { return rows; }
    int getColumns() { return columns; }


//    ArrayList<String> ReadLine(BufferedReader scn) {
//        ArrayList<String> ret = new ArrayList<>();
//        return ret;
//    }
//public static void main(String[] args) {
//    Chart c = new Chart(new File("./charts/Ewokese/Ewok 1.txt"));
//}
}
