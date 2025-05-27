package dto;

public class MatrixData {
    private double[] rowA;
    private double[][] matrixB;
    private double[] resultRow;

    public double[] getRowA() {
        return rowA;
    }

    public void setRowA(double[] rowA) {
        this.rowA = rowA;
    }

    public double[][] getMatrixB() {
        return matrixB;
    }

    public void setMatrixB(double[][] matrixB) {
        this.matrixB = matrixB;
    }

    public double[] getResultRow() {
        return resultRow;
    }

    public void setResultRow(double[] resultRow) {
        this.resultRow = resultRow;
    }
}