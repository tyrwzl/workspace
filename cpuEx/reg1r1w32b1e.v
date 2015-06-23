// 1R1W 32bit 1entry Register File
module reg1r1w32b1e(rd, wd, we, clk, n_rst);
  output [31:0] rd; 
  input  [31:0] wd;
  input we;
  input  clk, n_rst;

  reg [31:0] rf;

  always @(posedge clk or negedge n_rst) begin
    if (n_rst == 0)
      rf <= 0;
    else if (we == 1)
      rf <= wd;
  end

  assign rd = rf;

endmodule
