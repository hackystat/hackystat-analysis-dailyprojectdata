package org.hackystat.dailyprojectdata.resource.unittest;

import java.math.BigInteger;

/**
 * Encapsulates simple counting for UnitTestDPD member data.
 *
 * @author Pavel Senin.
 *
 */
public class UnitTestSimpleCounter {
  private Integer totalErrors;
  private Integer totalFailures;
  private Integer totalPassed;
  private Integer totalTests;

  /**
   * Default constructor. Inits values with 0.
   */
  public UnitTestSimpleCounter() {
    this.totalErrors = 0;
    this.totalFailures = 0;
    this.totalPassed = 0;
    this.totalTests = 0;
  }

  /**
   * Creates simple counter.
   *
   * @param error unit test errors counter initial value.
   * @param failure unit test failures counter initial value.
   * @param passed passed unit test counter initial value.
   * @param tests unit test counter initial value.
   */
  public UnitTestSimpleCounter(Integer error, Integer failure, Integer passed, Integer tests) {
    this.totalErrors = error;
    this.totalFailures = failure;
    this.totalPassed = passed;
    this.totalTests = tests;
  }

  /**
   * Updates internal values with numbers provided.
   *
   * @param error unit test errors counter update value.
   * @param failure unit test failures counter update value.
   * @param passed passed unit test counter update value.
   * @param tests unit test counter update value.
   */
  public void update(Integer error, Integer failure, Integer passed, Integer tests) {
    this.totalErrors += error;
    this.totalFailures += failure;
    this.totalPassed += passed;
    this.totalTests += tests;
  }

  /**
   * Reports member UnitTest success count.
   *
   * @return member UnitTest success count.
   */
  public BigInteger getSuccessCount() {
    return BigInteger.valueOf(this.totalPassed);
  }

  /**
   * Reports member UnitTest failures count.
   *
   * @return member UnitTest failures count.
   */
  public BigInteger getFailureCount() {
    return BigInteger.valueOf(this.totalFailures);
  }

  /**
   * Reports member UnitTest total tests count.
   *
   * @return member UnitTest tests count.
   */
  public BigInteger getTestCount() {
    return BigInteger.valueOf(this.totalTests);
  }

  /**
   * Reports member UnitTest total error count.
   *
   * @return member UnitTest error count.
   */
  public BigInteger getErrorsCount() {
    return BigInteger.valueOf(this.totalErrors);
  }

}
