// top-entity
`include "macro"

module cpu_ex(//ra1, ra2, wa, 
              //we_rf, we_sr, we_tr, we_dr,
              clk, n_rst_rf, n_rst_sr, n_rst_tr, n_rst_dr, n_rst_pg, n_rst_ir);

  //input [2:0] ra1, ra2, wa; // address
  //input we_rf, we_sr, we_tr, we_dr; // write enable
  input clk; // clock
  input n_rst_rf, n_rst_sr, n_rst_tr, n_rst_dr, n_rst_pg, n_rst_ir; // negative reset signal

  wire [31:0] rf_tr, rf_sr;
  wire [31:0] tr_add, sr_add;
  wire [31:0] add_dr, dr_rf;
  wire [31:0] ir;
  wire [`ph_w : `ph_f] phase;

  // phase generator
  phase_gen pg(hlt, phase, clk, n_rst_pg);
  // IR
  //reg1r1w32b1e ir(ir, ir_i, phase[`ph_f], clk, n_rst_ir);
  reg_ir reg_ir(ir, clk, n_rst_ir);
  // register file
  reg2r1w32b8e rf(ir[21:19], ir[18:16], ir[21:19], rf_tr, rf_sr, dr_rf, phase[`ph_w], clk, n_rst_rf);
  // TR
  reg1r1w32b1e tr(tr_add, rf_tr, phase[`ph_r], clk, n_rst_tr);
  // SR
  reg1r1w32b1e sr(sr_add, rf_sr, phase[`ph_r], clk, n_rst_sr);
  // adde
  adder add(tr_add, sr_add, add_dr);
  // DR
  reg1r1w32b1e dr(dr_rf, add_dr, phase[`ph_x], clk, n_rst_dr);

endmodule
