`include "macro"

module pc(phase, ct_taken, dr, pc, clk, n_rst);
  input  [`ph_w : `ph_f] phase;
  input                 ct_taken;
  input  [31:0]          dr;
  output [31:0]          pc;
  input                  clk, n_rst;

  reg [31:0] pc;
  always @(posedge clk or negedge n_rst) begin
    if (n_rst == 0)
      pc <= 0;
    else if (phase[`ph_f] == 1)
      pc <= pc + 4;
    else if (phase[`ph_w] == 1 && ct_taken == 1)
      pc <= dr;
  end
endmodule
