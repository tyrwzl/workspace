// 2R1W 32bit 8entry Register File
module  reg2r1w32b8e(ra1, ra2, wa, rd1, rd2, wd, we, clk, n_rst);
  input  [ 2:0] ra1, ra2, wa; // address
  output [31:0] rd1, rd2;     // read data
  input  [31:0] wd;           // write data
  input  we;                  // write enable signal
  input  clk, n_rst;          // control signal
// output reg [31:0] rf[0:7]; // for debug

  integer i;

  reg [31:0] rf [0:7];        // register file 
  always @(posedge clk  or  negedge n_rst) begin
    if (n_rst == 0) begin
      for (i = 0; i < 8; i = i + 1)
        rf[i] <= 0;
      rf[0] <= 1;
      rf[1] <= 2;
    end else if (we == 1) 
        rf[wa] <= wd;         // write
  end

  assign  rd1 = rf[ra1];      // read
  assign  rd2 = rf[ra2];      // read

endmodule
