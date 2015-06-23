// 1R1W 32bit 1entry Register File
module reg_ir(rd, clk, n_rst);
  output [31:0] rd;
  input  clk, n_rst;

  reg [31:0] rf;

  always @(posedge clk or negedge n_rst) begin
    if (n_rst == 0)
      rf <= 32'b00000000_11000001_00000000_00000000;
  end

  assign rd = rf;

endmodule
