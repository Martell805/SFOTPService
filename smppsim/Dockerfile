FROM bitsensedev/smpp-sim:latest
RUN sed -i 's/PASSWORDS=pass12345/PASSWORDS=pass123/' /smppsim/conf/smppsim.props \
    && sed -i 's/value="INFO"/value="DEBUG"/' /smppsim/conf/logback.xml \
    && sed -i 's/CAPTURE_SME_DECODED=false/CAPTURE_SME_DECODED=true/' /smppsim/conf/smppsim.props