#!/usr/bin/perl

%hash = ();

open(F,"table");
@LINES = <F>;
close(F);

foreach my $line (@LINES){
	$line =~ tr/\r\n//d;
	if($line =~ /^<tr>/){
		$count = 0;
	}
	if($count == 5 || $count == 3){
		if($line =~ /a href/){
			$line =~ s/<a href=".*">//g;
			$line =~ s/<\/a>//g;
			$line =~ s/<td>//g;
			$line =~ s/<\/td>//g;
			$hash{$line} = $line;
		}
	}
	$count++;
}

foreach my $k (keys %hash){
	print $k,"\n";
}
