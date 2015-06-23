// testbench file
`timescale 1ns / 1ps

module testbench;

  reg clk; // clock
  reg n_rst_rf, n_rst_tr, n_rst_sr, n_rst_dr, n_rst_pg, n_rst_ir;
  //reg [2:0] ra1, ra2, wa; // adress
  //reg we_rf, we_tr, we_sr, we_dr;

  // instance module
  cpu_ex top(//ra1, ra2, wa, we_rf, we_sr, we_tr, we_dr,
             clk, n_rst_rf, n_rst_sr, n_rst_tr, n_rst_dr, n_rst_pg, n_rst_ir);
  initial begin
    $dumpfile("test.vcd");
    $dumpvars;

    clk = 0;
    n_rst_rf = 0; n_rst_sr = 0; n_rst_tr = 0; n_rst_dr = 0; n_rst_pg = 0; n_rst_ir = 0;
    //ra1 = 0; ra2 = 0; wa = 0;
    //we_rf = 0; we_sr = 0; we_tr = 0; we_dr = 0;

    // wait 100 ns
    #100 
    n_rst_rf = 1; n_rst_sr = 1; n_rst_tr = 1; n_rst_dr = 1; n_rst_pg = 1; n_rst_ir = 1;
    // for hold-time
    // test vector
    //ra1 = 0; ra2 = 1; wa = 2;
    /*
    #30
    //we_sr = 1; we_tr = 1;
    #10
    //we_sr = 0; we_tr = 0; we_dr = 1;
    #10
    //we_dr = 0; we_rf = 1;
    #10
    //we_rf = 0;
    */
  end

  always begin
    #5 clk = ~ clk;
  end
endmodule
