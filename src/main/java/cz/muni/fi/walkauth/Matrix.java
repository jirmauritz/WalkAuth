package cz.muni.fi.walkauth;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents matrix of doubles and implements various matrix operations.
 *
 * Once the matrix is created, its dimension can't be changed. All math
 * operations create and return new matrix (and don't change the original
 * matrix). Nevertheless, the class is not immutable (contains a method to set
 * individual elements) to allow for applying functions to individual elements
 * cheaply.
 */
public final class Matrix {

    // number of rows
    private final int n;

    // number of columns
    private final int m;

    // values of all elements
    private final double[][] values;

    /**
     * Create a matrix with given values.
     *
     * @param values 2D array of values
     */
    public Matrix(double[][] values) {
        this.n = values.length;
        this.m = (this.n == 0) ? 0 : values[0].length;
        this.values = values;
    }

    /**
     * Create an uninitialized matrix of given size
     *
     * @param n number of rows
     * @param m number of columns
     */
    public Matrix(int n, int m) {
        this.n = n;
        this.m = m;
        this.values = new double[n][m];
    }

    /**
     * Static factory for an identity matrix.
     *
     * @param n	number of rows and columns
     * @return identity matrix of size n
     */
    public static Matrix identity(int n) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * Static factory for matrix with given number at all positions
     *
     * @param n	number of rows
     * @param m number of columns
     * @param value initial number at all positions
     * @return n-m matrix with given value at all positions
     */
    public static Matrix constant(int n, int m, double value) {
        Matrix a = new Matrix(n, m);
        
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                a.set(i, j, value);
            }
        }
        
        return a;
    }

    /**
     * Static factory for matrix with all zeros.
     *
     * @param n	number of rows
     * @param m number of columns
     * @return n-m matrix full of zeros
     */
    public static Matrix zeros(int n, int m) {
        return Matrix.constant(n, m, 0.0);
    }

    /**
     * Static factory for matrix with all ones.
     *
     * @param n	number of rows
     * @param m number of columns
     * @return n-m matrix full of ones
     */
    public static Matrix ones(int n, int m) {
        return Matrix.constant(n, m, 1.0);
    }

    /**
     * Static factory for matrix initialized with random numbers between 0 and 1
     *
     * @param n	number of rows
     * @param m number of columns
     * @return n-m matrix filled with random numbers
     */
    public static Matrix random(int n, int m) {
        double[][] values = new double[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                values[i][j] = Math.random();
            }
        }
        return new Matrix(values);
    }

    /**
     * @return number of rows
     */
    public int getRowCount() {
        return n;
    }

    /**
     * @return number of columns
     */
    public int getColCount() {
        return m;
    }

    /**
     * Return an element of matrix
     *
     * @param row row index of the element
     * @param col column index of the element
     * @return number at given position
     */
    public double get(int row, int col) {
        return values[row][col];
    }

    /**
     * Set an element of matrix
     *
     * @param row row index of the element
     * @param col column index of the element
     * @param number number to set at given position
     */
    public void set(int row, int col, double number) {
        values[row][col] = number;
    }

    /**
     * Add this matrix to another.
     *
     * @param other	the other matrix
     * @return new matrix with is the result of the addition
     */
    public Matrix add(Matrix other) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * Calculate this matrix minus another matrix.
     *
     * @param other	the other matrix (subtrahend)
     * @return new matrix with is the result of the subtraction
     */
    public Matrix subtract(Matrix other) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * Multiply this matrix with another.
     *
     * @param other	the other matrix
     * @return new matrix with is the result of the multiplication
     */
    public Matrix multiply(Matrix other) {
        if (this.getColCount() != other.getRowCount()) {
            throw new IllegalArgumentException("Cannot multiply matrices due to"
                    + " incompatible dimensions. The matrix has to have "
                    + this.getColCount() + " row(s), but it has "
                    + other.getRowCount());
        }

        Matrix product = zeros(this.getRowCount(), other.getColCount());

        for (int i = 0; i < this.getRowCount(); i++) {
            for (int j = 0; j < other.getColCount(); j++) {
                for (int k = 0; k < this.getColCount(); k++) {
                    product.set(i, j,
                            product.get(i, j)
                            + this.get(i, k) * other.get(k, j)
                    );
                }
            }
        }

        return product;
    }

    /**
     * Calculate dot product of two vectors.
     *
     * @param other	vector to calculate the dot product with
     * @return dot product (which is a single number)
     * @throws IllegalStateException if this matrix is not a vector
     * @throws IllegalArgumentException if the other matrix is not a vector or
     * the size does not match
     */
    public double dotProduct(Matrix other) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * Multiply this matrix by a scalar.
     *
     * @param scalar number with which to multiply the matrix
     * @return new matrix with is the result of the multiplication
     */
    public Matrix multiplyByScalar(double scalar) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * Create a new matrix by augmenting this matrix with another (to right).
     *
     * If this matrix has dimensions [n,m] and the other matrix has dimensions
     * [n,p], then the resulting matrix has dimensions [n, m + p].
     *
     * @param other	the other matrix
     * @return augmented matrix
     * @throws IllegalArgumentException if the other matrix has different number
     * of rows than this matrix
     */
    public Matrix augmentToRight(Matrix other) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * Create transposed matrix.
     *
     * @return transposition of the matrix
     */
    public Matrix transpose() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * Create a vector with all values from the matrix
     *
     * @return single vector containing all values from the matrix
     */
    public Matrix unpack() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * Create a list with all values from the matrix
     *
     * @return list containing all values from the matrix
     */
    public List<Double> toList() {
        List<Double> list = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                list.add(this.values[i][j]);
            }
        }
        return list;
    }

    @Override
    public String toString() {
        String repr = "Matrix [" + n + "x" + m + "]\n";
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                repr += String.format("%.3f ", this.values[i][j]);
            }
            repr += '\n';
        }
        return repr;
    }

}
